package com.ssafy.thxstore.controller.order;

import com.ssafy.thxstore.controller.config.AppProperties;
import com.ssafy.thxstore.controller.member.AuthController;
import com.ssafy.thxstore.controller.member.Resource.MemberResource;
import com.ssafy.thxstore.controller.order.Resource.ReviewResource;
import com.ssafy.thxstore.reservation.domain.Review;
import com.ssafy.thxstore.reservation.dto.ReservationDto;
import com.ssafy.thxstore.reservation.dto.ReservationGroupDto;
import com.ssafy.thxstore.reservation.dto.ReviewDto;
import com.ssafy.thxstore.reservation.dto.StatusRequest;
import com.ssafy.thxstore.reservation.service.ReservationService;
import com.ssafy.thxstore.reservation.service.ReviewService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RequestMapping(value = "/order", produces = MediaTypes.HAL_JSON_VALUE)
public class OrderController {
private final ReservationService reservationService;
private final ReviewService reviewService;
private final AppProperties appProperties;
/**
 * 주문 생성
 * 스토어의 매뉴 창에서 메뉴 선택 후 바로 주문 한다.  STAND_BY
 * 주문 테이블에 넣어줌
 * reservationStatus   --- >   STAND_BY   -> 사장님 승인 후 ->  ACCEPT   -> 사장님 수령 확인 완료 후  --> FINISH
 * [
     * {
     *
         * reser_id :12,(pk)
         * user_id :1,
         * store_id :3,
         * reservationStatus : stand_by
             * [{불닭 ,},{},{}] -> 그룹 엔티티 하나 만들고 연결하자   --  list  -- dto로 전할때는 build로 넣어줘
     *
     * },
 * ]
 *
 * 네 email jwt 받고 -> 주문상태변경  -> email로 검색해서 가게 사장님인지. 이해했어요!  네 알
 */


@PostMapping("/reservation")
public ResponseEntity<String> addReservation(@RequestHeader String authorization, @RequestBody ReservationDto reservation){

    String email = jwtToEmail(authorization);
    reservationService.addReservation(email,reservation);

    return new ResponseEntity<>("생성완료", HttpStatus.OK);
}

    /**
     * 주문 조회
     * 1. reservation_group 테이블에서 member_id 로 찾아서 List<ReservationGroup> 형식으로 리턴
     */

    //토큰 id 포함한 객체로 받았으면 더 좋았을듯
    @GetMapping("/reservation/member")
    public ResponseEntity getReservation(@RequestHeader String authorization){

        String email = jwtToEmail(authorization);
        List<ReservationDto> li = reservationService.getReservation(email,"member");

        return new ResponseEntity<>(li, HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

//    /**
//     * 사장님 입장 주문 갱신
//     * ->pusher 이벤트 들어오면 한번 호출 -> 새롭게 들어온 주문 목록을 리턴한다.
//     */
//
//    @GetMapping("/reservation/store/{storeId}")
//    public ResponseEntity reflashReservation(@PathVariable Long storeId){
//
//        List<ReservationDto> li = reservationService.getReservation(storeId,"store");
//        return new ResponseEntity<>(li, HttpStatus.OK);
////        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
//    }

    /**
     *  사장님 or 사용자의 주문 취소 버튼 클릭 후 후 -> 테이블 자체에서 삭제
     *  member_id와 store_id(사장님의 email 토큰) 로 reservation 테이블에서 삭제한다.
     *  사장님 email 토큰은 로그인 한 사장님만 알 수 있음 ->
     */

    /**
     * 회원의 주문 취소  authorization -> 회원의 email 토큰 -> 이걸로 member id 가져온다
     */
    @DeleteMapping("/reservation/member")
    public ResponseEntity deleteReservationForMember(@RequestHeader String authorization,@RequestParam("memberId") Long storeId){
        String email = jwtToEmail(authorization);
        reservationService.deleteReservation(email,storeId,"member");

        return new ResponseEntity<>("주문 취소 되었습니다.", HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

    /**
     * 사장님의 주문 취소
     */
    @DeleteMapping("/reservation/store")
    public ResponseEntity deleteReservationForStore(@RequestHeader String authorization,@RequestParam("storeId") Long memberId){
        String email = jwtToEmail(authorization);
        reservationService.deleteReservation(email,memberId,"store");

        return new ResponseEntity<>("주문 취소 되었습니다.", HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

    /**
     * 1. 주문 테이블에 들어간 상황 사장님이 수령 확인 버튼 누르면 주문 status 변경 memberId(321) 님 이시죠? 물건 주고 버튼 누르면 주문 테이블에서 상태 변화
     *
     * 2. DEFAULT -> ACCEPT 주문 승락 버튼
     * 3. ACCEPT -> STAND_BY 상품(음식) 조리 완료 후 수령 대기 버튼
     * 4. STAND_BY -> FINISH 수령 완료 버튼
     */


    /**
     * 사장님 -> 토큰 ,  유저 아디이 받고 ,  스토어 아이디 받아서   -스토어테이블에서   스토어 아이디
     * 취소 변경
     */

    @PutMapping("/reservation/status") // v2 mem id로 받아서 검색 후 수정, 받아오는 형식 memformdto
    public ResponseEntity<String> updateStatus(@RequestHeader String authorization,@RequestBody StatusRequest status) {
        String email = jwtToEmail(authorization);

        reservationService.statusUpdate(email,status);

        return new ResponseEntity<>("주문 상태를 변경했습니다.", HttpStatus.OK);
    }//맴버정보보기를 눌러서 확인

    /**
     * 사장님 입장에서 조회 ->본인의 가게에 들어온 주문 내역만
     *
     * 사장님이 조회 페이지 눌렀을 경우 푸셔 인스턴스 만들고
     */

    @GetMapping("/reservation/store")
    public ResponseEntity getStoreReservation(@RequestHeader String authorization){
        String email = jwtToEmail(authorization);
        List<ReservationDto> li = reservationService.getReservation(email,"store");
        return new ResponseEntity<>(li, HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

    /**
     * 타임딜 관련해서 채크 여러개 했을 때 구매가 불가능한 품목만 리턴
     */

    /**
     * 리뷰 생성 삭제 수정
     */
    @PostMapping("/reservation/review")
    public ResponseEntity createReview(@RequestBody ReviewDto reviewDto){
        Review newReview = reviewService.createReview(reviewDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(OrderController.class).slash("reservation/review").slash(newReview.getId());
        URI createUri = selfLinkBuilder.toUri();

        ReviewResource reviewResource = new ReviewResource(newReview);
        reviewResource.add(Link.of("/api/docs/index.html#resources-create-review").withRel("profile"));

        return ResponseEntity.created(createUri).body(reviewResource);
    }

    @DeleteMapping("/reservation/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){

        reviewService.deleteReview(reviewId);

        return new ResponseEntity<>("삭제완료", HttpStatus.OK);
    }

    @PutMapping("/reservation/review/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,@RequestBody ReviewDto reviewDto){

        reviewService.updateReview(reviewId, reviewDto);

        return new ResponseEntity<>("수정완료", HttpStatus.OK);
    }

    /**
     * 사장님 답변
     */

    /**
     * 리뷰 조회 -> 내 리뷰 조회, 스토어의 리뷰 조회 받는 형식 -> 사용자의아이디 글내용 별점 int
     * datetime 조회
     */

    @GetMapping("/reservation/review/{memberId}")
    public ResponseEntity getReviewBymember(@PathVariable Long memberId){

        List<ReviewDto> ReviewList = reviewService.getReview(memberId,"member");

        return new ResponseEntity<>(ReviewList, HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

    @GetMapping("/reservation/review/store/{storeId}")
    public ResponseEntity getReviewByStore(@PathVariable Long storeId){

        List<ReviewDto> ReviewList = reviewService.getReview(storeId,"store");

        return new ResponseEntity<>(ReviewList, HttpStatus.OK);
//        return ResponseEntity.created(li.getUri()).body(li.getOrderResource());
    }

    public String jwtToEmail(String authorization){
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(appProperties.getAuth().getTokenSecret()))
                .parseClaimsJws(authorization).getBody().getSubject();
    }
}

/**
 * 주문 승락 상태에서 주문 취소 들어오면 오류 반환 예외처리하자
 */

/**
 * 리스트 리스폰스 형식 배열 이쁘게~
 */