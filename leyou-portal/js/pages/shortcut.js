const shortcut = {
    template: "\
    <div class='py-container'> \
        <div class='shortcut'> \
            <ul class='fl'> \
               <li class='f-item'>¡Bienvenid@ a Leyou Mall!&ensp;</li> \
               <li class='f-item' v-if='user && user.username'>\
               Estimad@，<span style='color: cadetblue; font-weight: bold'>{{user.username}}</span>\
               </li>\
               <li v-else class='f-item'> \
                   <a  @click='gotoLogin'>Iniciar Sesión</a>　 \
                   <span><a @click='gotoRegister' target='_blank' style='color: slateblue'>Regístrate</a></span> \
               </li> \
           </ul> \
           <ul class='fr'> \
               <li class='f-item'>Mis pedidos</li> \
               <li class='f-item space'></li> \
               <li class='f-item'><a href='home.html' target='_blank'>Mi cuenta</a></li> \
               <li class='f-item space'></li> \
               <li class='f-item'>Leyou Prime</li> \
               <li class='f-item space'></li> \
               <li class='f-item'>Compras</li> \
               <li class='f-item space'></li> \
               <li class='f-item'>NewsLetter</li> \
               <li class='f-item space'></li> \
               <li class='f-item' id='service'> \
                   <span>Atención al cliente</span> \
                   <ul class='service'> \
                       <li><a href='cooperation.html' target='_blank'>Servicio1</a></li> \
                       <li><a href='shoplogin.html' target='_blank'>Servicio2</a></li> \
                       <li><a href='cooperation.html' target='_blank'>Servicio3</a></li> \
                       <li><a href='#'>Servicio4</a></li> \
                   </ul> \
               </li> \
               <li class='f-item space'></li> \
               <li class='f-item'>Navigación</li> \
           </ul> \
       </div> \
    </div>\
    ",
    name: "shortcut",
    data() {
        return {
            user: null
        }
    },
    created() {
        ly.http("/auth/verify")
            .then(resp => {
                this.user = resp.data;
            })
    },
    methods: {
        gotoLogin() {
            window.location = "http://www.leyou.com/login.html?returnUrl=" + window.location;
        },
        gotoRegister() {
            window.location = "http://www.leyou.com/register.html";
        }
    }
}
export default shortcut;