(function () {
    const btn = document.getElementById('menuBtn');
    const menu = document.getElementById('mobileMenu');
    if (!btn || !menu) return;

    btn.addEventListener('click', () => {
        const isOpen = !menu.classList.contains('hidden');
        menu.classList.toggle('hidden');
        btn.setAttribute('aria-expanded', String(!isOpen));
    });

    menu.querySelectorAll('a').forEach(a => {
        a.addEventListener('click', () => {
            menu.classList.add('hidden');
            btn.setAttribute('aria-expanded', 'false');
        });
    });
})();

function toggleProject(projectId) {
    const details = document.getElementById('details-' + projectId);
    const arrow = document.getElementById('arrow-' + projectId);

    if (!details || !arrow) return;

    const isCurrentlyOpen = details.classList.contains('active');

    if (isCurrentlyOpen) {
        details.classList.remove('active');
        arrow.classList.remove('rotated');
    } else {
        details.classList.add('active');
        arrow.classList.add('rotated');
    }
}

function isMobile() {
    return window.matchMedia('(max-width: 767px)').matches;
}

function openImageOverlay(src) {
    const overlay = document.getElementById('imageOverlay');
    const img = document.getElementById('overlayImage');
    if (!overlay || !img) return;

    img.src = src;
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeImageOverlay() {
    const overlay = document.getElementById('imageOverlay');
    const img = document.getElementById('overlayImage');
    if (!overlay || !img) return;

    overlay.classList.remove('active');
    img.src = '';
    document.body.style.overflow = '';
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.zoomable-image').forEach(function (img) {
        img.addEventListener('click', function (e) {
            if (isMobile()) return;
            e.preventDefault();
            e.stopPropagation();
            openImageOverlay(this.src);
        });
    });
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeImageOverlay();
    }
});

document.getElementById('overlayImage').addEventListener('click', function (e) {
    e.stopPropagation();
});