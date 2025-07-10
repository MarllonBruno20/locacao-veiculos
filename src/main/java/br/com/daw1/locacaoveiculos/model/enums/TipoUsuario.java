package br.com.daw1.locacaoveiculos.model.enums;

public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    CLIENTE("Cliente");

    private final String displayValue;

    TipoUsuario(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}