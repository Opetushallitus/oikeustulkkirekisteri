package fi.vm.sade.oikeustulkkirekisteri.util;

import fi.vm.sade.auditlog.User;
import fi.vm.sade.javautils.http.HttpServletRequestUtils;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public final class AuditUtil {

    private AuditUtil() {
    }

    public static User getUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return getUser(((ServletRequestAttributes) requestAttributes).getRequest());
        }
        return new User(getIp(), null, null);
    }

    public static User getUser(HttpServletRequest request) {
        InetAddress inetAddress = getInetAddress(request);
        String session = request.getSession().getId();
        String userAgent = request.getHeader("User-Agent");
        return getOid().map(oid -> new User(oid, inetAddress, session, userAgent))
                .orElseGet(() -> new User(inetAddress, session,userAgent));
    }

    public static InetAddress getIp() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return InetAddress.getLoopbackAddress();
        }
    }

    private static Optional<Oid> getOid() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication -> authentication.isAuthenticated())
                .flatMap(authentication -> Optional.ofNullable(authentication.getName()))
                .map(oid -> {
                    try {
                        return new Oid(oid);
                    } catch (GSSException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static InetAddress getInetAddress(HttpServletRequest request) {
        String remoteAddress = HttpServletRequestUtils.getRemoteAddress(request);
        try {
            return InetAddress.getByName(remoteAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
