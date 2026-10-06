# syntax=docker/dockerfile:1

# ============================================================================
# QuizCraft - Production image
#
# Stage 1: compile the WAR with Maven (Java 17, matching pom.xml)
# Stage 2: run it on Tomcat 10.1 (Jakarta EE 10 / Servlet 6.0)
#
# The WAR is deployed as ROOT.war so the context path is empty:
#   app root  -> /
#   login     -> /login
# ============================================================================

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Resolve dependencies first so this layer is cached until pom.xml changes.
# `|| true` keeps the build resilient: go-offline can miss optional plugins, and
# the real compile step below downloads anything that is still missing.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

# Copy sources and build the WAR.
COPY src ./src
RUN mvn -B -q clean package -DskipTests


# ---------- Stage 2: runtime ----------
FROM tomcat:10.1-jre17-temurin

LABEL org.opencontainers.image.title="QuizCraft" \
      org.opencontainers.image.description="Online Java quiz platform (Servlet + JSP + MariaDB)"

# ---------------------------------------------------------------------------
# Listen on the port Render provides.
# Tomcat's server.xml supports ${...} replacement from system properties, so the
# default Connector port is swapped for a property that setenv.sh supplies from
# $PORT (falling back to 8080 for plain `docker run` without PORT).
# ---------------------------------------------------------------------------
RUN sed -i 's/port="8080"/port="${httpPort}"/' "${CATALINA_HOME}/conf/server.xml" \
 && printf '%s\n' \
      '#!/bin/sh' \
      '# Render supplies the public port via $PORT.' \
      'export CATALINA_OPTS="${CATALINA_OPTS} -DhttpPort=${PORT:-8080}"' \
      > "${CATALINA_HOME}/bin/setenv.sh" \
 && chmod +x "${CATALINA_HOME}/bin/setenv.sh"

# Remove the bundled sample apps and management tools that are unused and would
# only add surface area to the deployed image.
RUN rm -rf "${CATALINA_HOME}/webapps/ROOT" \
           "${CATALINA_HOME}/webapps/docs" \
           "${CATALINA_HOME}/webapps/examples" \
           "${CATALINA_HOME}/webapps/host-manager" \
           "${CATALINA_HOME}/webapps/manager"

# Deploy as ROOT.war -> empty context path -> app served from "/" with
# /login, /register, /home, /quiz, /result and /logout directly available.
COPY --from=build /build/target/quizcraft-1.0-SNAPSHOT.war "${CATALINA_HOME}/webapps/ROOT.war"

# Render supplies the port at runtime via $PORT (no EXPOSE value is meaningful
# for a dynamically assigned port); Tomcat is ready once it is listening there.
CMD ["catalina.sh", "run"]