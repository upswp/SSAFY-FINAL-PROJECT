<template>
  <div class="user-account-container">
    <div class="page-title">프로필 설정</div>
    <div class="user-account-contents">
      <div class="profile-background"></div>
      <div class="user-contents-wrapper">
        <div class="user-profile-image">
          <img :src="userData.profileImage" />
        </div>
        <div class="profile-change-button">
          <label for="image-upload-button">
            <awesome icon="plus"></awesome>
          </label>
          <input id="image-upload-button" type="file" style="display: none" @change="changeImage" />
        </div>
        <div class="user-profile-info">
          <div class="simple-info">
            <div class="col">
              <div class="email">
                <div class="item-header">이메일</div>
                <div class="item-label">{{ userData.email }}</div>
              </div>
              <div class="social">
                <div class="item-header">소셜 가입</div>
                <div class="item-label">{{ userData.social === 'LOCAL' ? '일반 회원' : '소셜 회원' }}</div>
              </div>
            </div>
            <div class="col">
              <div class="nickname">
                <div class="item-header">
                  닉네임
                  <awesome icon="pen-square" @click="change.nickname = true"></awesome>
                </div>
                <transition name="fade" mode="out-in">
                  <div v-if="!change.nickname" key="non-editable" class="item-display">
                    <div class="item-label">{{ userData.nickname }}</div>
                  </div>
                  <div v-else key="editable" class="item-input">
                    <input v-model="userData.nickname" type="text" maxlength="10" @keydown.enter="setNickname" />
                    <awesome :icon="['far', 'check-circle']" @click="setNickname"></awesome>
                  </div>
                </transition>
              </div>
              <div class="phone">
                <div class="item-header">
                  핸드폰 번호
                  <awesome icon="pen-square" @click="change.phone = true"></awesome>
                </div>
                <transition name="fade" mode="out-in">
                  <div v-if="!change.phone" key="non-editable" class="item-display">
                    <div class="item-label">{{ userData.phoneNumber }}</div>
                  </div>
                  <div v-else key="editable" class="item-input">
                    <input v-model="userData.phoneNumber" type="tel" @keydown.enter="setPhoneNumber" />
                    <awesome :icon="['far', 'check-circle']" @click="setPhoneNumber"></awesome>
                  </div>
                </transition>
              </div>
            </div>
          </div>
          <hr />
          <div class="address">
            <div class="item-header">
              위치 설정
              <awesome icon="pen-square" @click="loaded = true"></awesome>
            </div>
            <div class="item-display address">
              <div class="item-label">{{ userData.address }}</div>
            </div>
            <div class="kakao-map">
              <kakao-map :location="newLocation" @lat-update="latUpdate" @lon-update="lonUpdate"></kakao-map>
            </div>
          </div>
        </div>
      </div>
    </div>
    <set-road-name v-if="loaded" @newAddress="setAddress"></set-road-name>
  </div>
</template>

<script>
import KakaoMap from '@/components/common/KakaoMap';
import SetRoadName from '@/components/common/SetRoadName';
import { mapGetters } from 'vuex';

import { updateProfile } from '@/api/profile';
import { saveUserToLocalStorage } from '@/utils/webStorage';
export default {
  components: {
    KakaoMap,
    SetRoadName,
  },
  data() {
    return {
      userData: {},
      change: {
        nickname: false,
        phone: false,
        address: false,
      },
      loaded: false,
      newLocation: '대전 유성구 동서대로 98-39',
    };
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  created() {
    this.userData = Object.assign({}, this.getUserInfo);
    this.newLocation = this.userData.address;
  },
  methods: {
    latUpdate(e) {
      this.userData.lat = e;
    },
    lonUpdate(e) {
      this.userData.lon = e;
    },
    async setNickname() {
      this.change.nickname = false;
      const frm = new FormData();
      frm.append('id', this.userData.id);
      frm.append('nickname', this.userData.nickname);
      try {
        await updateProfile(frm);
        saveUserToLocalStorage(this.userData);
        this.change.nickname = false;
      } catch (error) {
        alert('닉네임 변경 실패!');
      }
    },
    async setPhoneNumber() {
      this.change.phone = false;
      const frm = new FormData();
      frm.append('id', this.userData.id);
      frm.append('phoneNumber', this.userData.phoneNumber);
      try {
        await updateProfile(frm);
        saveUserToLocalStorage(this.userData);
        this.change.phone = false;
      } catch (error) {
        alert('핸드폰 번호 변경 실패!');
      }
    },
    async setAddress(addr) {
      this.loaded = false;
      if (!addr) return;
      const frm = new FormData();
      frm.append('id', this.userData.id);
      frm.append('address', addr);
      frm.append('lat', this.userData.lat);
      frm.append('lon', this.userData.lon);
      this.userData.address = addr;
      this.newLocation = addr;
      try {
        await updateProfile(frm);
        saveUserToLocalStorage(this.userData);
      } catch (error) {
        alert('위치 변경 실패!');
      }
    },
    async changeImage(e) {
      const file = e.target.files[0];
      try {
        const frm = new FormData();
        frm.append('id', this.userData.id);
        frm.append('profileImage', file);
        await updateProfile(frm);
        this.userData.profileImage = URL.createObjectURL(file);
      } catch (error) {
        alert('프로필 이미지 변경 실패!');
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.user-account-container {
  width: 80%;
  @include mobile() {
    width: 100%;
  }
  @include xs-mobile() {
    width: 100%;
  }
}
.profile-background {
  width: 100%;
  height: 130px;
  background: black;
  @include mobile() {
    height: 110px;
  }
  @include xs-mobile() {
    height: 100px;
  }
}
.page-title {
  font-size: 24px;
  text-align: center;
  font-weight: 600;
  margin-bottom: 20px;
  @include mobile() {
    font-size: 18px;
  }
  @include xs-mobile() {
    font-size: 16px;
  }
}
.user-account-contents {
  border: 1px solid $gray100;
  border-radius: 5px;
  background: white;
  @include shadow1;
  padding: 20px;
}
.user-contents-wrapper {
  position: relative;
  top: -110px;
  @include mobile() {
    top: -90px;
  }
  @include xs-mobile() {
    top: -70px;
  }
}
.user-profile-image {
  @include flexbox;
  @include justify-content(center);
  & > img {
    width: 160px;
    height: 160px;
    border-radius: 50%;
    object-fit: cover;
    padding: 5px;
    background-color: white;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
    @include mobile {
      width: 140px;
      height: 140px;
    }
    @include xs-mobile {
      width: 110px;
      height: 110px;
    }
  }
}

.profile-change-button {
  @include flexbox;
  @include justify-content(center);
  & > label > svg {
    position: relative;
    top: -50px;
    right: -70px;
    width: 30px;
    height: 30px;
    padding: 10px;
    border-radius: 50%;
    background-color: $gray600;
    color: white;
    box-sizing: content-box;
    cursor: pointer;
    @include mobile() {
      right: -60px;
      width: 28px;
      height: 28px;
    }
    @include xs-mobile() {
      top: -45px;
      right: -45px;
      width: 24px;
      height: 24px;
    }
  }
}
.email,
.social,
.nickname,
.phone {
  margin-bottom: 30px;
  text-align: center;
}
.item-header {
  font-size: 20px;
  font-weight: 600;
  height: 20px;
  margin-bottom: 20px;
  text-align: center;
  svg {
    width: 20px;
    height: 20px;
    color: $blue300;
    cursor: pointer;
    &:hover {
      color: $blue600;
    }
    @include mobile() {
      width: 16px;
      height: 16px;
    }
    @include xs-mobile() {
      width: 14px;
      height: 14px;
    }
  }
  @include mobile() {
    font-size: 16px;
    height: 16px;
  }
  @include xs-mobile() {
    font-size: 14px;
    height: 14px;
  }
}
.item-display {
  @include flexbox;
  @include justify-content(center);
  @include align-items(center);
  &.address {
    margin-bottom: 20px;
  }
}

.nickname,
.phone {
  input {
    border: none;
    border-bottom: 1px solid $gray400;
    width: 130px;
    text-align: center;
    margin-right: 5px;
  }
}
.address {
  input {
    border: none;
    border-bottom: 1px solid $gray400;
    min-width: 200px;
    text-align: center;
    margin-right: 5px;
  }
}

.simple-info {
  @include flexbox;
  @include pc() {
    @include justify-content(space-around);
  }
  margin-bottom: 10px;
}
hr {
  width: 80%;
  margin-bottom: 20px;
}
.col {
  width: 50%;
  @include lg-pc() {
    @include flexbox;
    @include justify-content(space-around);
  }
  text-align: center;
}
.item-input {
  @include flexbox;
  @include align-items(center);
  @include justify-content(center);
  svg {
    color: $gray600;
    width: 20px;
    height: 20px;
    cursor: pointer;
    &:hover {
      color: $green800;
    }
  }
}
.item-label {
  min-width: 130px;
  line-height: 20px;
  width: 100%;
  text-align: center;
  @include ellipsis-one;
  @include mobile {
    font-size: 14px;
  }
  @include xs-mobile {
    font-size: 12px;
  }
}
.kakao-map {
  @include flexbox;
  @include justify-content(center);
}
.map-container {
  width: 80%;
}
@include fade-transition(fade, 0.5s);
</style>
