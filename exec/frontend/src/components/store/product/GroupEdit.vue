<template>
  <div class="edit-container">
    <div class="edit-container-inner">
      <div class="go-retrieve" @click="$emit('toggleEdit', false)">메뉴 조회로 이동하기</div>
      <section class="edit-wrapper">
        <label for="upload-button" class="upload-button-wrapper">
          <img :src="menu.productImg" class="menu-image" />
          <input id="upload-button" type="file" @change="uploadImg" />
        </label>
        <div class="menu-name">
          <label for="menu-name-label">
            <div class="menu-label">상품명</div>
            <input
              id="menu-name-label"
              v-model="menu.name"
              type="text"
              class="menu-input"
              placeholder="name"
              maxlength="30"
            />
          </label>
        </div>
        <div class="menu-price-amount">
          <div class="menu-price">
            <label for="menu-price-label">
              <div class="menu-label">가격</div>
              <input
                id="menu-price-label"
                v-model="menu.price"
                type="text"
                class="menu-input"
                placeholder="price"
                maxlength="10"
              />
            </label>
          </div>
          <div class="menu-amount">
            <label for="menu-amount-label">
              <div class="menu-label">단위</div>
              <input
                id="menu-amount-label"
                v-model="menu.amount"
                type="text"
                class="menu-input"
                placeholder="amount"
                maxlength="20"
              />
            </label>
          </div>
        </div>
        <div class="menu-intro">
          <label for="menu-intro-label">
            <div class="menu-label">메뉴 설명</div>
            <textarea id="menu-intro-label" v-model="menu.introduce" class="menu-textarea" maxlength="300"></textarea>
          </label>
        </div>
        <button class="submit-button" @click="submit">완료</button>
      </section>
    </div>
  </div>
</template>
<script>
import { getMenu, registerMenuByGroup, updateMenu } from '@/api/menu';
import { mapMutations } from 'vuex';
export default {
  props: {
    productId: {
      type: Number,
      default: -1,
      require: true,
    },
    groupId: {
      type: Number,
      default: -1,
      require: true,
    },
  },
  data() {
    return {
      menu: {
        name: '',
        price: '',
        amount: '',
        introduce: '',
        productImg: require('@/assets/image/uploadGuide.jpg'),
      },
      origin: {},
      menuFile: '',
    };
  },
  computed: {
    validateForm() {
      return (
        this.menu.name !== '' &&
        this.menu.price !== '' &&
        this.menu.amount !== '' &&
        this.menu.introduce !== '' &&
        this.menu.menuFile !== ''
      );
    },
  },
  async created() {
    if (this.productId === -1) return;
    try {
      const { data } = await getMenu(this.productId);
      this.origin = data;
      this.menu = Object.assign(this.menu, data);
    } catch (error) {
      console.log(error);
      alert('메뉴 상세 조회에 실패하였습니다.');
    }
  },
  methods: {
    ...mapMutations(['setSpinnerState']),
    uploadImg(e) {
      const file = e.target.files[0];
      this.menuFile = file;
      this.menu.productImg = URL.createObjectURL(file);
    },
    async createMenu() {
      try {
        this.setSpinnerState(true);
        const frm = new FormData();
        frm.append('groupId', this.groupId);
        frm.append('name', this.menu.name);
        frm.append('price', this.menu.price);
        frm.append('amount', this.menu.amount);
        frm.append('introduce', this.menu.introduce);
        frm.append('productImg', this.menuFile);
        await registerMenuByGroup(frm);
        this.setSpinnerState(false);
        this.$emit('toggleEdit', false);
      } catch (error) {
        console.log(error);
        this.setSpinnerState(false);
        alert('메뉴 등록에 실패하였습니다.');
      }
    },
    async updateMenu() {
      try {
        this.setSpinnerState(true);
        const frm = new FormData();
        frm.append('productId', this.productId);
        if (this.origin.name !== this.menu.name) frm.append('name', this.menu.name);
        if (this.origin.price !== this.menu.price) frm.append('price', this.menu.price);
        if (this.origin.amount !== this.menu.amount) frm.append('amount', this.menu.amount);
        if (this.origin.introduce !== this.menu.introduce) frm.append('introduce', this.menu.introduce);
        if (this.menuFile) frm.append('productImg', this.menuFile);

        await updateMenu(frm);
        this.setSpinnerState(false);
        this.$emit('toggleEdit', false);
      } catch (error) {
        console.log(error);
        this.setSpinnerState(false);
        alert('메뉴 수정에 실패하였습니다.');
      }
    },
    submit() {
      if (!this.validateForm) {
        alert('모든 빈칸을 채워주세요');
        return;
      }

      if (this.productId === -1) this.createMenu();
      else this.updateMenu();
    },
  },
};
</script>

<style lang="scss" scoped>
.edit-container {
  width: calc(100% - 200px);
  @include flexbox;
  @include justify-content(center);
  @include align-items(center);
  @include pc {
    width: 100%;
  }
  @include mobile {
    font-size: 14px;
    width: 100%;
  }
  @include xs-mobile {
    font-size: 12px;
    width: 100%;
  }
}
.edit-image-wrapper {
  width: clamp(300px, 100%, 600px);
}
.edit-wrapper {
  width: clamp(300px, 100%, 600px);
  padding: 10px;
  box-shadow: 0 5px 5px rgba(0, 0, 0, 0.2);
}
.menu-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  object-position: 50% center;
  cursor: pointer;
}
.menu-name,
.menu-intro {
  padding: 5px;
  margin-bottom: 5px;
}
.menu-price,
.menu-amount {
  width: 50%;
  padding: 5px;
  margin-bottom: 5px;
}

.menu-input {
  border: none;
  padding: 5px 0;
  width: 100%;
  border-bottom: 1px solid $gray400;
}
.menu-label {
  font-weight: bold;
  font-size: 18px;
  margin-bottom: 3px;
  @include mobile {
    font-size: 14px;
  }
  @include xs-mobile {
    font-size: 12px;
  }
}
.menu-price-amount {
  @include flexbox;
}
.menu-textarea {
  min-height: 100px;
  border: none;
  padding: 5px 0;
  width: 100%;
  border: 1px solid $gray400;
  resize: none;
}
#upload-button {
  display: none;
}
.submit-button {
  width: 100%;
  background-color: $gray100;
  border: none;
  transition: all 0.2s;
  &:hover {
    background-color: $purple600;
    color: white;
  }
}
.go-retrieve {
  width: clamp(300px, 100%, 600px);
  cursor: pointer;
  color: $gray400;
  text-align: right;
  margin: 10px 0 8px 0;
  &:hover {
    color: $navy400;
  }
}
.edit-container-inner {
  width: clamp(300px, 100%, 600px);
}
</style>
