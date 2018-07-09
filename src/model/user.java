package model;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class user implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Blob pic;

    @Getter
    @Setter
    private Date date;
}
