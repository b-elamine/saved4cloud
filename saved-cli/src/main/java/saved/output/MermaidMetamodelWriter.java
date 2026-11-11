package saved.output;   

import saved.metamodel.MClass;
import saved.metamodel.MPackage;
import saved.metamodel.MAttribute;
import saved.metamodel.MReference;

public  final class MermaidMetamodelWriter {
    
    private  MermaidMetamodelWriter(){}
    
    public static String generate(MPackage pkg) {
            
        StringBuilder sb = new StringBuilder();
        sb.append("classDiagram\n");

        for (MClass cls : pkg.classes()){
            sb.append("    class ").append(cls.name()).append(" {\n");
            if (cls.abstractClass()) {
                sb.append("     <<abstract>>\n");
            }
            for (MAttribute a : cls.attributes()){
                sb.append("     ")
                .append(a.type()).append(" ")
                .append(a.name())
                .append(" [").append(mult(a.lowerBound(), a.upperBound())).append("]");

                if (a.isEnum()) {
                    sb.append(" : ").append(String.join("|", a.allowed()));
                }
                sb.append("\n");
            }
            
            sb.append("     }\n");
        }
            

        for (MClass cls : pkg.classes()) {
            for (String parent : cls.superTypes()){
                sb.append("     ").append(parent).append(" <|-- ").append(cls.name()).append("\n");
            }
        }

        for (MClass cls : pkg.classes()){
            for (MReference r : cls.references()){
                String arrow = r.containment() ? " *-- " : " --> ";
                sb.append("     ")
                .append(cls.name())
                .append(arrow)
                .append("\"").append(mult(r.lowerBound(), r.upperBound())).append("\" ")
                .append(r.targetClass())
                .append(" : ").append(r.name()).append("\n");
            }
        }

        return  sb.toString();
        
    }

    private static String mult(int lo, int hi){
        String max = (hi == -1) ? "*" : String.valueOf(hi);
        return lo + ".." + max; 
    }
        
}