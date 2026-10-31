
document.addEventListener("DOMContentLoaded", () => {
    const mensagemLogout = document.querySelector(".mensagem.logout");

    // Se não existe mensagem de logout, não faz nada.
    if (!mensagemLogout) {
        return;
    }

    // Remove o parâmetro logout do endereço para evitar
    // que a mensagem reapareça ao atualizar a página.
    const url = new URL(window.location.href);

    if (url.searchParams.has("logout")) {
        url.searchParams.delete("logout");

        window.history.replaceState(
            {},
            document.title,
            url.pathname + url.search + url.hash
        );
    }

    // Esconde a mensagem após 3 segundos.
    window.setTimeout(() => {
        mensagemLogout.hidden = true;
    }, 3000);
});
