(function () {
    const btn = document.getElementById('menuBtn');
    const menu = document.getElementById('mobileMenu');

    if (!btn || !menu) return;

    btn.addEventListener('click', () => {
        menu.classList.toggle('hidden');
        const expanded = btn.getAttribute('aria-expanded') === 'true';
        btn.setAttribute('aria-expanded', String(!expanded));
    });

    menu.querySelectorAll('a').forEach((a) => {
        a.addEventListener('click', () => {
            menu.classList.add('hidden');
            btn.setAttribute('aria-expanded', 'false');
        });
    });
})();