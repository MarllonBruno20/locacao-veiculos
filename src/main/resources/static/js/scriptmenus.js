// src/main/resources/static/js/scriptmenus.js

// Declaração das variáveis do menu mobile
const btn = document.getElementById('mobile-menu-button');
const menu = document.getElementById('mobile-menu');

// Declaração das variáveis do menu de usuário (se existirem)
const btn_usuario = document.getElementById('menu-usuario-btn'); // Verifique se este ID existe no seu HTML
const menu_usuario = document.getElementById('menu-usuario');   // Verifique se este ID existe no seu HTML

// Lógica para o menu principal mobile
if (btn) {
    btn.addEventListener('click', navToggleMenu);
}

function navToggleMenu() {
  if (btn && menu) {
    btn.classList.toggle('open');
    menu.classList.toggle('flex');
    menu.classList.toggle('hidden');
    // Adicionei verificações para btn_usuario existir antes de tentar manipular
    if (btn.classList.contains('open')) {
      if (btn_usuario) btn_usuario.classList.add('hidden');
    } else {
      if (btn_usuario) btn_usuario.classList.remove('hidden');
    }
  }
}

// Lógica para o menu de usuário
if (btn_usuario) { // Verifica se o botão de usuário existe
    btn_usuario.addEventListener('click', navToggleUsuario);
}

function navToggleUsuario() {
  if (menu_usuario) { // Verifica se o menu de usuário existe
    menu_usuario.classList.toggle('flex');
    menu_usuario.classList.toggle('hidden');
    // Adicionei verificações para btn existir antes de tentar manipular
    if (!menu_usuario.classList.contains('hidden')) {
      if (btn) btn.classList.add('hidden');
    } else {
      if (btn) btn.classList.remove('hidden');
    }
  }
}