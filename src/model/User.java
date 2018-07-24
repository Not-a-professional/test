package model;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private byte[] pic;

    @Getter
    @Setter
    private Date date;

    @Getter
    @Setter
    private int age;
}
