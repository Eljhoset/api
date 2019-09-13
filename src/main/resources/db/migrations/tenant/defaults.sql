INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('client', '$2a$10$1GVjxAP3DzN5LMBNRyOgxOGkrxRGOi5qD1KPgMLzLum41ux/qColC', 'read,write','password,client_credentials,refresh_token',
     null, null, 10000, 30000, null, true);

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('ui-short-live-client', '$2a$10$1GVjxAP3DzN5LMBNRyOgxOGkrxRGOi5qD1KPgMLzLum41ux/qColC', 'read,write','password,client_credentials,refresh_token', null, null, 15, 30000, null, true);

INSERT INTO users (username,password)
VALUES
('admin','$2a$10$/YsEmU7WVIcrvxzpV16FpOgEeC/y05hVVIlQ7taJVa.5jF94CkyRq');

INSERT INTO users_roles(username,roles)
VALUES
('admin','ADMIN');