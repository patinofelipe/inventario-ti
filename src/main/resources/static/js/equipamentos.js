
    (() => {
        const botao = document.getElementById('alternarImportacao');
        const painel = document.getElementById('painelImportacao');

        if (!botao || !painel) {
            return;
        }

        botao.addEventListener('click', () => {
            const aberto = painel.classList.toggle('aberto');

            botao.setAttribute('aria-expanded', String(aberto));
            painel.setAttribute('aria-hidden', String(!aberto));
        });
    })();
