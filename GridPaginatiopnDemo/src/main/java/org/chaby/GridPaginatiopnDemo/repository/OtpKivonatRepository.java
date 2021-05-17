package org.chaby.GridPaginatiopnDemo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.chaby.GridPaginatiopnDemo.entity.OtpKivonat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpKivonatRepository extends JpaRepository<OtpKivonat, String>, JpaSpecificationExecutor<OtpKivonat> {
    OtpKivonat findByTrazon(String trazon);
    @Query(value =
            "select *\n" +
            "from   otp_kivonat x \n" +
            "where  date(x.kdat) = :kdat \n" +
            "and    x.osszeg = :osszeg \n" +
            "and    x.forg_tip like 'Kártyás művelet%'\n" +
            "and    length(trazon) > 4",
            nativeQuery = true)
    List<OtpKivonat> searchCardTransaction(String kdat, BigDecimal osszeg);
}
