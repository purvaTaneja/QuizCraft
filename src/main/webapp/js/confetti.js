/**
 * QuizCraft - lightweight confetti celebration.
 *
 * Self-contained canvas implementation with no external dependencies.
 * The canvas is created on demand and removed once the animation finishes,
 * so nothing is left behind in the DOM.
 */
(function (global) {
    "use strict";

    // Colours chosen to sit comfortably inside the light blue / indigo theme.
    var COLORS = [
        "#2563eb", // primary
        "#3b82f6", // primary light
        "#60a5fa", // secondary
        "#0ea5e9", // accent
        "#16a34a", // success
        "#f59e0b"  // warning
    ];

    function Confetti() {
        this.canvas = null;
        this.ctx = null;
        this.particles = [];
        this.running = false;
        this.animationId = null;
    }

    Confetti.prototype.createCanvas = function () {
        if (this.canvas) {
            return;
        }
        var canvas = document.createElement("canvas");
        canvas.style.position = "fixed";
        canvas.style.top = "0";
        canvas.style.left = "0";
        canvas.style.width = "100%";
        canvas.style.height = "100%";
        canvas.style.pointerEvents = "none";
        canvas.style.zIndex = "9999";
        document.body.appendChild(canvas);
        this.canvas = canvas;
        this.ctx = canvas.getContext("2d");
        this.resize();
    };

    Confetti.prototype.resize = function () {
        if (!this.canvas) {
            return;
        }
        var ratio = global.devicePixelRatio || 1;
        this.canvas.width = global.innerWidth * ratio;
        this.canvas.height = global.innerHeight * ratio;
        this.canvas.style.width = global.innerWidth + "px";
        this.canvas.style.height = global.innerHeight + "px";
        if (this.ctx) {
            this.ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
        }
    };

    Confetti.prototype.spawn = function (originX, originY, count, power) {
        for (var i = 0; i < count; i++) {
            this.particles.push({
                x: originX,
                y: originY,
                size: Math.random() * 8 + 4,
                speedX: (Math.random() - 0.5) * power,
                speedY: Math.random() * -power - 2,
                gravity: Math.random() * 0.18 + 0.05,
                drag: 0.99,
                rotation: Math.random() * 360,
                rotationSpeed: (Math.random() - 0.5) * 0.3,
                color: COLORS[Math.floor(Math.random() * COLORS.length)],
                alpha: 1,
                wobble: Math.random() * Math.PI * 2
            });
        }
    };

    Confetti.prototype.step = function () {
        var ctx = this.ctx;
        var width = global.innerWidth;
        var height = global.innerHeight;

        ctx.clearRect(0, 0, width, height);

        var alive = 0;
        for (var i = 0; i < this.particles.length; i++) {
            var p = this.particles[i];

            p.wobble += 0.05;
            p.speedY += p.gravity;
            p.speedX += Math.sin(p.wobble) * 0.4;
            p.speedX *= p.drag;
            p.speedY *= p.drag;

            p.x += p.speedX;
            p.y += p.speedY;
            p.rotation += p.rotationSpeed;

            if (p.y > height + 40) {
                p.alpha -= 0.012;
            }

            if (p.alpha > 0) {
                alive++;

                ctx.save();
                ctx.globalAlpha = Math.max(0, Math.min(1, p.alpha));
                ctx.translate(p.x, p.y);
                ctx.rotate((p.rotation * Math.PI) / 180);
                ctx.fillStyle = p.color;
                ctx.fillRect(-p.size / 2, -p.size / 2, p.size, p.size * 1.6);
                ctx.restore();
            }
        }
        return alive;
    };

    Confetti.prototype.stop = function () {
        this.running = false;
        if (this.animationId) {
            global.cancelAnimationFrame(this.animationId);
            this.animationId = null;
        }
        if (this.onResize) {
            global.removeEventListener("resize", this.onResize);
            this.onResize = null;
        }
        if (this.canvas && this.canvas.parentNode) {
            this.canvas.parentNode.removeChild(this.canvas);
        }
        this.canvas = null;
        this.ctx = null;
        this.particles = [];
    };

    Confetti.prototype.loop = function () {
        if (!this.running) {
            return;
        }
        var alive = this.step();

        // Only finish once particles have existed and all of them have died.
        // An empty list means the staggered bursts are still pending, so the
        // loop must keep going rather than tearing the canvas down.
        if (this.particles.length > 0 && alive <= 0) {
            this.stop();
            return;
        }

        this.animationId = global.requestAnimationFrame(this.loop.bind(this));
    };

    /**
     * Fires a celebration burst from two lower corners plus a centre burst.
     */
    Confetti.prototype.celebrate = function () {
        if (this.running) {
            return;
        }

        this.createCanvas();
        this.running = true;

        var self = this;
        var onResize = function () {
            self.resize();
        };
        this.onResize = onResize;
        global.addEventListener("resize", onResize);

        var width = global.innerWidth;
        var height = global.innerHeight;

        // The opening burst is spawned SYNCHRONOUSLY so particles already exist
        // before the animation loop starts. Starting the loop first would make
        // step() report zero live particles and tear the canvas down instantly.
        this.spawn(width * 0.12, height * 0.85, 70, 14);
        this.spawn(width * 0.88, height * 0.85, 70, 14);

        // Additional staggered bursts, added while the loop is already running.
        global.setTimeout(function () {
            self.spawn(width * 0.5, height * 0.42, 90, 17);
        }, 180);

        global.setTimeout(function () {
            self.spawn(width * 0.3, height * 0.5, 45, 13);
            self.spawn(width * 0.7, height * 0.5, 45, 13);
        }, 340);

        this.loop();
    };

    global.QuizCraftConfetti = new Confetti();
})(window);
