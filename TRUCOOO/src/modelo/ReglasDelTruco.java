package modelo;

import java.util.List;

public class ReglasDelTruco {
    public static int valorTruco(Carta c){
        int v=c.getValor(); String p=c.getPalo();
        switch(v){
            case 4:return 1;
            case 5:return 2;
            case 6:return 3;
            case 7:
                if(p.equals("Espadas"))return 12;
                if(p.equals("Oros"))return 11;
                return 4;
            case 10:return 5;
            case 11:return 6;
            case 12:return 7;
            case 1:
                if(p.equals("Espadas"))return 14;
                if(p.equals("Bastos"))return 13;
                return 8;
            case 2:return 9;
            case 3:return 10;
        }
        return 0;
    }

    public static int valorEnvidoMano(List<Carta> m){
        int max=0;
        for(int i=0;i<m.size();i++){
            for(int j=i+1;j<m.size();j++){
                if(m.get(i).getPalo().equals(m.get(j).getPalo())){
                    int s=m.get(    i).valorEnvido()+m.get(j).valorEnvido()+20;
                    if(s>max)max=s;
                }
            }
        }
        if(max>0)return max;
        for(Carta c:m)if(c.valorEnvido()>max)max=c.valorEnvido();
        return max;
    }

    public static boolean tieneFlor(List<Carta> m){
        if(m.size()!=3)return false;
        String p=m.get(0).getPalo();
        return m.get(1).getPalo().equals(p)&&m.get(2).getPalo().equals(p);
    }

    public static int valorFlor(List<Carta> m){
        int s=0;
        for(Carta c:m)s+=c.valorEnvido();
        return s+20;
    }

    public static int valorEnvido(List<Carta> mano) {
        return valorEnvidoMano(mano);
    }

}
