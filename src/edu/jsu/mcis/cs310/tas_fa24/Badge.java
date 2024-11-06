package edu.jsu.mcis.cs310.tas_fa24;

import java.util.zip.CRC32;


public class Badge {

    private final String id, description;

    public Badge(String id, String description) {
        this.id = id;
        this.description = description;
    }
    public Badge(String description) {
        
        this.description = description;
        this.id = makeId();
         
         
         
    }
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append('#').append(id).append(' ');
        s.append('(').append(description).append(')');

        return s.toString();

    }
    public final String makeId(){
        CRC32 cr = new CRC32();
        System.out.println(description);
        cr.update(description.getBytes());
         
        long checkSum = cr.getValue();
        System.out.println(String.format("%08X", checkSum));
        return String.format("%08X", checkSum);
    }

}
