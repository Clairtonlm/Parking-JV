(function () {
    const tipoSelect = document.getElementById('tipoVeiculo');
    const vagaSelect = document.getElementById('vagaSelect');

    if (!tipoSelect || !vagaSelect) {
        return;
    }

    function filtrarVagas() {
        const tipo = tipoSelect.value;
        let primeiraLivre = null;

        Array.from(vagaSelect.options).forEach(function (opt) {
            if (!opt.value) {
                return;
            }
            const combina = opt.dataset.tipo === tipo;
            opt.hidden = !combina;
            opt.disabled = !combina;
            if (combina && primeiraLivre === null) {
                primeiraLivre = opt;
            }
        });

        const selecionada = vagaSelect.selectedOptions[0];
        if (!selecionada || selecionada.disabled || selecionada.hidden) {
            vagaSelect.value = primeiraLivre ? primeiraLivre.value : '';
        }
    }

    tipoSelect.addEventListener('change', filtrarVagas);
    filtrarVagas();
})();
