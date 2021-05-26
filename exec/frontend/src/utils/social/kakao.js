import { socialLogin, socialSignup } from '@/utils/social';

export default {
  init() {
    window.Kakao.init(process.env.VUE_APP_KAKAO_API_KEY);
    return true;
  },

  getInfo(authObj, division) {
    window.Kakao.API.request({
      url: '/v2/user/me',
      success: async res => {
        const kakao_account = res.kakao_account;
        const req = {
          userId: res.id,
          nickname: kakao_account.profile.nickname,
          email: kakao_account.email,
          profileImage: kakao_account.profile.profile_image_url,
          social: 'KAKAO',
        };
        if (division === 'login') {
          socialLogin(req);
        } else {
          socialSignup(req);
        }
      },
      fail: error => {
        console.log(error);
        alert('카카오 로그인 사용자 정보를 가져오지 못했습니다.');
      },
    });
  },

  login() {
    window.Kakao.Auth.login({
      scope: 'profile, account_email',
      success: authObj => {
        this.getInfo(authObj, 'login');
      },
      fail: error => {
        console.log(error);
        alert('카카오로 로그인 실패');
      },
    });
  },

  signup() {
    window.Kakao.Auth.login({
      scope: 'profile, account_email',
      success: authObj => {
        this.getInfo(authObj, 'signup');
      },
      fail: error => {
        console.log(error);
        alert('카카오로 회원가입 실패');
      },
    });
  },

  logout() {
    window.Kakao.Auth.logout(res => {
      if (!res) {
        return console.log(error);
      }
      social_logout();
    });
  },
};
