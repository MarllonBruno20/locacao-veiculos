// delete_events.js

// Lógica para o evento de deleção de USUÁRIO
document.body.addEventListener('usuarioDeletado', function(evt) {
    // Este evento é disparado pelo AdminUsuarioController após uma deleção bem-sucedida.
    // Recarregamos a página para atualizar a lista.
    window.location.reload();
});

// Lógica para o evento de deleção de PESSOA
document.body.addEventListener('pessoaDeletada', function(evt) {
    // Este evento pode ser disparado pelo AdminPessoaController.
    // Se o backend enviou uma mensagem de erro (por exemplo, pessoa vinculada), exibe.
    if (evt.detail && evt.detail.mensagemErro) {
        alert('Erro ao deletar pessoa: ' + evt.detail.mensagemErro);
    } else {
        // Se não houve erro, a deleção deve ter sido bem-sucedida e o HTMX
        // já deve ter aplicado o 'hx-swap="outerHTML"' para remover o elemento.
        // Recarregar a página é uma garantia extra ou para casos onde o swap não é total.
        window.location.reload();
    }
});