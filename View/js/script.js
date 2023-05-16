window.onload = () => {
    const app = new Vue({
        el: '#app',
        data: {
            value: 15
        },
        methods: {
            change(value) {
                this.value = value;
            }
        },
        mounted() {
            setTimeout(() => {
                setInterval(() => {
                    this.value += 20;
                }, 800);
            }, 1000);
        },
        components: {
            'xc-progress': {
                template: '#xcprogress',
                props: {
                    value: {
                        type: Number,
                        default: 0
                    }
                },
                watch: {
                    value(newVal) {
                        if (newVal > 100) {
                            this.value = 100;
                        } else if (newVal < 0) {
                            this.value = 0;
                        }
                        this.$emit('change', this.value);
                    }
                }
            }
        }
    })
}