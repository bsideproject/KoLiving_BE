INSERT IGNORE INTO LANGUAGE (LOCALE, MESSAGE_KEY, MESSAGE_PATTERN) VALUES
       ('en', 'home_welcome','Welcome'),
       ('ko', 'home_welcome','환영합니다'),

       ('en', 'email_duplication','This email already exists : {0}'),
       ('ko', 'email_duplication','이미 존재하는 이메일입니다 : {0}'),
       ('en', 'auth_email_subject','Confirm your email'),
       ('ko', 'auth_email_subject','이메일 인증을 완료하세요'),
       ('en', 'auth_email_subtitle','Click the link below to proceed with authentication'),
       ('ko', 'auth_email_subtitle','아래 링크를 클릭하셔서 인증을 진행하세요'),
       ('en', 'auth_email_link_guidance','If the button doesn''t work, copy and paste the link below into url'),
       ('ko', 'auth_email_link_guidance','버튼이 동작하지 않다면, 아래 링크를 url에 붙여 인증을 시도하세요'),
       ('en', 'expired_confirmation_token','The confirmation token has expired'),
       ('ko', 'expired_confirmation_token','만료된 확인 토큰입니다'),
       ('en', 'authenticated_confirmation_token','The confirmation token already confirmed'),
       ('ko', 'authenticated_confirmation_token token','이미 인증된 확인 토큰입니다'),
       ('en', 'ungenerated_confirmation_token','The confirmation token is not a normal generated by the server'),
       ('ko', 'ungenerated_confirmation_token','서버에 의해 생성된 정상 확인 토큰이 아닙니다'),

       ('en', 'login_exception','The email or password you requested is invalid'),
       ('ko', 'login_exception','이메일 혹은 비밀번호가 유효하지 않습니다'),

       ('en', 'not_found_auth_header', 'Authorization header not found'),
       ('ko', 'not_found_auth_header', '인증 헤더에 토큰 정보가 없습니다'),
       ('en', 'not_bearer_type', 'Authorization header does not start with Bearer'),
       ('ko', 'not_bearer_type', '인증 헤더의 타입은 ''Bearer'' 입니다'),
       ('en', 'expired_token', 'Access token has expired'),
       ('ko', 'expired_token', '만료된 엑세스 토큰입니다'),
       ('en', 'malformed_token', 'Malformed jwt token'),
       ('ko', 'malformed_token', '잘못된 형식의 액세스 토큰입니다'),
       ('en', 'signature_invalid_token', 'Signature validation failed'),
       ('ko', 'signature_invalid_token', '인증부에서 검증 실패'),
       ('en', 'format_invalid_token', 'Unexpected format of jwt token'),
       ('ko', 'format_invalid_token', '지원하지 않는 형식의 토큰입니다'),

       ('en', 'Access Denied', 'Unauthorized request'),
       ('ko', 'Access Denied', '권한이 없는 요청입니다'),

       ('en', 'email_not_exists', 'This email doesn''t exist : {0}'),
       ('ko', 'email_not_exists', '존재하지 않는 이메일입니다 : {0}'),

       ('en', 'mismatched_password', 'The Password and verification password does not match'),
       ('ko', 'mismatched_password', '비밀번호가 확인 비밀번호와 일치하지 않습니다')
;
