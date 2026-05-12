//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package models;

import java.util.List;

public class Statistiques {
    private int totalPatients;
    private int totalRendezVous;
    private List<StatTypeTraitement> statsTypeTraitement;
    private List<StatStatutRdv> statsStatutRdv;

    public int getTotalPatients() {
        return this.totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public int getTotalRendezVous() {
        return this.totalRendezVous;
    }

    public void setTotalRendezVous(int totalRendezVous) {
        this.totalRendezVous = totalRendezVous;
    }

    public List<StatTypeTraitement> getStatsTypeTraitement() {
        return this.statsTypeTraitement;
    }

    public void setStatsTypeTraitement(List<StatTypeTraitement> statsTypeTraitement) {
        this.statsTypeTraitement = statsTypeTraitement;
    }

    public List<StatStatutRdv> getStatsStatutRdv() {
        return this.statsStatutRdv;
    }

    public void setStatsStatutRdv(List<StatStatutRdv> statsStatutRdv) {
        this.statsStatutRdv = statsStatutRdv;
    }

    public static class StatTypeTraitement {
        private String typeTraitement;
        private int nombre;

        public StatTypeTraitement(String typeTraitement, int nombre) {
            this.typeTraitement = typeTraitement;
            this.nombre = nombre;
        }

        public String getTypeTraitement() {
            return this.typeTraitement;
        }

        public void setTypeTraitement(String typeTraitement) {
            this.typeTraitement = typeTraitement;
        }

        public int getNombre() {
            return this.nombre;
        }

        public void setNombre(int nombre) {
            this.nombre = nombre;
        }
    }

    public static class StatStatutRdv {
        private String statut;
        private int nombre;

        public StatStatutRdv(String statut, int nombre) {
            this.statut = statut;
            this.nombre = nombre;
        }

        public String getStatut() {
            return this.statut;
        }

        public void setStatut(String statut) {
            this.statut = statut;
        }

        public int getNombre() {
            return this.nombre;
        }

        public void setNombre(int nombre) {
            this.nombre = nombre;
        }
    }
}
