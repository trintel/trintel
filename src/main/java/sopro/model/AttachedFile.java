package sopro.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class AttachedFile {

    @Getter @Setter @Id private long id = IdHandler.generateId();
    @Getter @Setter private String fileName;
    @Getter @Setter private String fileType;
    @Getter @Setter @Lob byte[] data;
    @Getter @Setter @ManyToOne private Action action;


    public AttachedFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

}
