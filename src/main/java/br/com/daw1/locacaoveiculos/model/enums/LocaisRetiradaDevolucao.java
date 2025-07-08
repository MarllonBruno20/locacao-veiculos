package br.com.daw1.locacaoveiculos.model.enums;

public enum LocaisRetiradaDevolucao {
    UberabaCentro("Uberaba Centro"),
    UberabaShopping("Uberaba Shopping"),
    UberlandiaCentro("Uberlândia Centro"),
    UberlandiaAeroporto("Uberlândia Aeroporto");

    private final String nomeFormatado;

    LocaisRetiradaDevolucao(String nomeFormatado) {
        this.nomeFormatado = nomeFormatado;
    }

    public String getNomeFormatado() {
        return nomeFormatado;
    }
    
    @Override
    public String toString() {
        return nomeFormatado;
    }
}
