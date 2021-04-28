package com.ssafy.thxstore.member.domain;

import com.ssafy.thxstore.common.ColumnDescription;
import com.ssafy.thxstore.reservation.domain.Reservation;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_gen")
    @SequenceGenerator(
            name = "user_sequence_gen",
            sequenceName = "user_sequence"
    )
    @ColumnDescription("PK")
    private Long id;

    @ColumnDescription("유저가 좋아하는 가게 리스트")
    @OneToMany
    private List<Favorite> storeList = new LinkedList<>();

    @ColumnDescription("유저가 주문한 리스트[오더리스트]")
    @OneToMany
    private List<Reservation> reservationList = new ArrayList<>();

    @ColumnDescription("소셜로그인 유저 ID")
    @Column(name = "user_id", unique = true)
    private String userId;

    @Email
    @ColumnDescription("유저 이메일")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ColumnDescription("유저 비밀번호")
    @Column(name = "password", nullable = false)
    private String password;

    @ColumnDescription("유저 위치정보")
    @Column(name = "address")
    private String address;

    @ColumnDescription("유저 닉네임")
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickName;

    @ColumnDescription("유저 프로필 이미지")
    @Column(name = "image")
    private String image;

    @ColumnDescription("유저 휴대폰 번호")
    @Column(name = "phone_number")
    private String phoneNumber;

    @ColumnDescription("유저 Role")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "member_type")
    private Set<MemberRole> roles;

    @ColumnDescription("소셜로그인 유저 정보")
    @Enumerated(EnumType.STRING)
    @Column(name = "social")
    private Social social;

    @Builder
    public Member(String userId, @Email String email, String password, String address, String nickName, String image, String phoneNumber, Set<MemberRole> roles, Social social) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.address = address;
        this.nickName = nickName;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.social = social;
    }

    public Object getRoles(MemberRole user) {
        return MemberRole.USER;
    }

    public Object getSocial(Social local) {
        return Social.LOCAL;
    }
}
