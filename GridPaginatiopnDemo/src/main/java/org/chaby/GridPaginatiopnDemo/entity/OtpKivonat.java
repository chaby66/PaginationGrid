package org.chaby.GridPaginatiopnDemo.entity;


import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "otp_kivonat")
public class OtpKivonat {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private String forgTip;
    @Temporal(TemporalType.DATE)
    private Date kdat;
    @Temporal(TemporalType.DATE)
    private Date ertNap;
    private BigDecimal osszeg;
    private BigDecimal konyv;
    private String eszla;
    private String enev;
    private String kozl;
    private String trazon;

    @Generated(GenerationTime.INSERT)
    private Integer partnerId;
//    @ManyToOne
//    @JoinColumn(name = "partner_id")
//    private OtpPartner partnerId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpKivonat that = (OtpKivonat) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
