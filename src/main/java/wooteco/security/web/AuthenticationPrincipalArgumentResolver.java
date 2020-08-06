package wooteco.security.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import wooteco.security.core.Authentication;
import wooteco.security.core.AuthenticationPrincipal;
import wooteco.security.core.context.SecurityContextHolder;
import wooteco.subway.maps.map.annotation.RequiredPath;

import java.util.Arrays;
import java.util.Map;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        boolean isPath = parameter.hasMethodAnnotation(RequiredPath.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isPath) {
            if (authentication == null) {
                return null;
            }
        }
        if (authentication == null) {
            throw new AuthorizationException();
        }
        if (authentication.getPrincipal() instanceof Map) {
            return extractPrincipal(parameter, authentication);
        }

        return authentication.getPrincipal();
    }

    private Object extractPrincipal(MethodParameter parameter, Authentication authentication) {
        try {
            Map<String, String> principal = (Map) authentication.getPrincipal();

            Object[] params = Arrays.stream(parameter.getParameterType().getDeclaredFields())
                    .map(it -> toObject(it.getType(), principal.get(it.getName())))
                    .toArray();

            return parameter.getParameterType().getConstructors()[0].newInstance(params);
        } catch (Exception e) {
            throw new AuthorizationException();
        }
    }

    public static Object toObject(Class clazz, String value) {
        if (Boolean.class == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz) return Byte.parseByte(value);
        if (Short.class == clazz) return Short.parseShort(value);
        if (Integer.class == clazz) return Integer.parseInt(value);
        if (Long.class == clazz) return Long.parseLong(value);
        if (Float.class == clazz) return Float.parseFloat(value);
        if (Double.class == clazz) return Double.parseDouble(value);
        return value;
    }
}
