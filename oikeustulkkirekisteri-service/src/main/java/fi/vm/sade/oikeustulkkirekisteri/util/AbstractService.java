package fi.vm.sade.oikeustulkkirekisteri.util;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.javautils.http.HttpServletRequestUtils;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * User: tommiratamaa
 * Date: 13.6.2016
 * Time: 13.42
 */
public abstract class AbstractService {
    protected Audit auditLog = new Audit(new LoggerImpl(), "oikeustulkkirekisteri", ApplicationType.VIRKAILIJA);

    protected User getUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        InetAddress inetAddress = getInetAddress(request);
        String session = request.getSession().getId();
        String userAgent = request.getHeader("User-Agent");
        return getOid().map(oid -> new User(oid, inetAddress, session, userAgent))
                .orElseGet(() -> new User(inetAddress, session,userAgent));
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
