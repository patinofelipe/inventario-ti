
    function formatarNrp(valor) {
        const digitos = valor.replace(/\D/g, "").slice(0, 6);

        if (digitos.length <= 3) {
            return digitos;
        }

        return digitos.slice(0, 3) + "." + digitos.slice(3);
    }

    function inicializarMascaraNrp() {
        const campo = document.getElementById("nrp");

        if (!campo) {
            return;
        }

        campo.value = formatarNrp(campo.value);

        campo.addEventListener("input", function () {
            campo.value = formatarNrp(campo.value);
        });
    }

    document.addEventListener("DOMContentLoaded", inicializarMascaraNrp);
