package model;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;

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
    private byte[] pic;

    @Getter
    @Setter
    private Timestamp date;

    @Getter
    @Setter
    private int age;
}
