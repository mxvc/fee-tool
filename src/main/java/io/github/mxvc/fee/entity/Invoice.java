package io.github.mxvc.fee.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"code", "number"}))
@FieldNameConstants
@ToString
public class Invoice {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;


    String name;

    // 10-增值税电子普通发票，04-增值税普通发票，01-增值税专用发票；
    String type;


    String code;


    String number;


    Date date;


    String validateCode;


    BigDecimal rate;

    BigDecimal amt;


    BigDecimal taxAmt;

    BigDecimal totalAmt;

    Date updateTime;

    @JsonIgnore
    @Lob
    @Lazy
    byte[] content;

    String remark;



    String owner;



    @PreUpdate
    @PrePersist
    public void prePersist(){
        this.updateTime = new Date();
    }




    @Transient
    public String getTypeLabel() {
        if (type == null) {
            return null;
        }

        switch (type) {
            case "10":
                return "增值税电子普通发票";
            case "04":
                return "增值税普通发票";
            case "01":
                return "增值税专用发票";
            case "31":
                return "电子发票（增值税专用发票）";
            case "32":
                return "电子发票（普通发票）";
        }
        return null;
    }

    @Transient
    public String getTypeLabelCrec() {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "10":
                return "普通发票";
        }
        return getTypeLabel();
    }



}
