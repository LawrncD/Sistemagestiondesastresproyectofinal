package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;

public class Admin extends Usuario {

    public Admin(String id, String nombre, String passwd, String numero) {
        super(id, nombre, passwd, numero, "ADMIN");
    }
    public void asignarRecursos(SistemaGestionDesastres sistema, String zonaDestino, TipoRecurso tipo, int cantidad) {
    boolean ok = sistema.getMapaRecursos().transferirRecursos("almacen-central", zonaDestino, tipo, cantidad);
    if (ok)
        System.out.println("✅ Recursos transferidos a " + zonaDestino + ": " + tipo + " x" + cantidad);
    else
        System.out.println("❌ No fue posible transferir recursos (verifique stock o zona destino)");
}

public void mostrarInventario(SistemaGestionDesastres sistema) {
    sistema.getMapaRecursos().mostrarInventarioGlobal();
}

    // Métodos de alto nivel (implementaciones simples o delegan a servicios)
    public boolean agregarTipoRecurso(TipoRecurso tipo, String descripcion, int prioridad) {
        // En fase 1 solo un stub: la implementación real estará en MapaRecursos/servicios.
        return true;
    }

    public boolean actualizarStockRecurso(TipoRecurso tipoRecurso, int cantidad) {
        // Stub: delegar a MapaRecursos en fases siguientes
        return true;
    }

}