package sopro.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class AttachedFile {

    @Getter @Setter @Id private long id = IdHandler.generateId();
    @Getter @Setter private String fileName;
    @Getter @Setter private String fileType; //TODO restrict to pdf??
    @Getter @Setter @Lob byte[] data;
    @Getter @Setter @OneToOne private Action action;


    public AttachedFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

}
