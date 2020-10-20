package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {
    BRAND_NOT_FOUND(404,"Marca no existe"),
    CATEGORY_NOT_FOUND(404,"Catálogo no encuentra"),
    BRAND_SAVE_ERROR(500,"servidor: Marca no se guarda"),
    CATEGORY_BRAND_SAVE_ERROR(500,"servidor: Catálogo de marca no se guarda"),
    UPLOAD_FILE_ERROR(500,"Cargar fichero fracasado"),
    INVALID_FILE_TYPE(400,"Tipo de fichero inválido"),
    SPEC_GROUP_NOT_FOUND(404,"Grupo de especificaciones no existe"),
    SPEC_PARAM_NOT_FOUND(404,"Parámetro de especificaciones no encuentra "),
    GOODS_NOT_FOUND(404,"Artículo no encuentra"),
    GOODS_SAVE_ERROR(500,"guardar artículo con fallo"),
    GOODS_DETAIL_NOT_FOUND(404,"Detalles de artículo no existen"),
    GOODS_SKU_NOT_FOUND(404,"SKU no existe"),
    GOODS_STOCK_NOT_FOUND(404,"stock de artículo no encuentra"),
    GOODS_UPDATE_ERROR(500,"actualizar artículo con fallo"),
    GOODS_ID_CANNOT_BE_NULL(400,"Id de artículo no puede ser nulo"),
    INVALID_USER_DATA_TYPE(400,"Tipo de data de usuario es inválido"),
    INVALID_VERIFY_CODE(400,"el código de verificación es inválido"),
    INVALID_USERNAME_PASSWORD(400,"la clave de usuario es inválido"),
    CREATE_TOKEN_ERROR(500,"crear token con fallo"),
    UN_AUTHORIZED(403,"no está autorizado"),
    CART_NOT_FOUND(404,"carrito no existe"),
    STOCK_NOT_FOUND(404,"stock no encuentra" ),
    RECEIVER_ADDRESS_NOT_FOUND(404,"la dirección de destinatario no encuentra" ),
    CREATED_ORDER_ERROR(500,"crear pedido con fallo" ),
    STOCK_NOT_ENOUGH(500, "stock insuficiente"),
    ORDER_NOT_FOUND(404,"pedido no existe" ),
    ORDER_DETAIL_NOT_FOUND(404,"detalle de pedido no encuentra" ),
    ORDER_STATUS_NOT_FOUND(404,"el estado de pedido no encuentra" ),
    WX_PAY_ORDER_FAIL(500,"wechat pay fracasado" ),
    ORDER_STATUS_ERROR(400,"el estado de pedido anormal" ),
    INVALID_SIGN_ERROR(400,"signatura inválido" ),
    INVALID_ORDER_PARAM(400,"parámetro del importe es inválido" ),
    UPDATE_ORDER_STATUS_ERROR(500,"actualizar el estado de pedido con fallo" ),
    IS_NOT_AN_ADMIN(500,"el usuario no es administrador");
    private int code;
    private String msg;
}
