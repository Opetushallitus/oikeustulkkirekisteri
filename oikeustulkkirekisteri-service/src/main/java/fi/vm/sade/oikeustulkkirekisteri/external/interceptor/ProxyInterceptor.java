package fi.vm.sade.oikeustulkkirekisteri.external.interceptor;

import fi.vm.sade.authentication.cas.CasApplicationAsAUserInterceptor;
import fi.vm.sade.authentication.cas.CasFriendlyCxfInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 15.28
 */
public class ProxyInterceptor extends AbstractPhaseInterceptor<Message> {
    @Autowired
    private ApplicationContext applicationContext;
    private Interceptor<Message> target;
    
    @Value("${rekisteri.external.apis.dev.mode:true}")
    private boolean devMode=false;
    @Value("${web.url.cas:}")
    private String webCasUrl;
    @Value("${rekisteri.external.apis.caller.service:oikeustulkkirekisteri}")
    private String callerService;
    @Getter @Setter
    private String serviceUrl;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;

    public ProxyInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    public ProxyInterceptor(String serviceUrl) {
        this();
        this.serviceUrl = serviceUrl;
    }
    
    public ProxyInterceptor(String serviceUrl, String username, String password) {
        this();
        this.serviceUrl = serviceUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    protected void init() {
        if (this.target != null) {
            return;
        }
        if (this.username != null && this.password !=null) {
            CasApplicationAsAUserInterceptor auth = new CasApplicationAsAUserInterceptor();
            // TODO: caller service? better implementation?
            auth.setWebCasUrl(this.webCasUrl);
            auth.setTargetService(this.serviceUrl);
            auth.setAppClientUsername(this.username);
            auth.setAppClientPassword(this.password);
            auth.setTicketCachePolicy(new NoOpTicketCachePolicy()); // oppijanumerorekisteri ei säilö tikettejä
            this.target = auth;
        } else if (this.devMode) {
            CasFriendlyCxfInterceptor f = new CasFriendlyCxfInterceptor();
            f.setUseBasicAuthentication(true);
            f.setUseSessionPerUser(true);
            f.setAppClientUsername(this.username);
            f.setUseBlockingConcurrent(true);
            f.setAppClientPassword(this.password);
            this.target = f;
        } else  {
            // TODO: find better implementation
            CasFriendlyCxfInterceptor f = new CasFriendlyCxfInterceptor();
            f.setUseBasicAuthentication(false);
            f.setCallerService(this.callerService);
            f.setUseSessionPerUser(true);
            f.setUseBlockingConcurrent(true);
            this.target = f;
        }
        this.applicationContext.getAutowireCapableBeanFactory().autowireBean(this.target);
    }
    
    @Override
    public void handleFault(Message message) {
        this.target.handleFault(message);
    }
    
    @Override
    public void handleMessage(Message message) throws Fault {
        this.target.handleMessage(message);
    }

    public Interceptor<Message> getTarget() {
        return target;
    }

    public void setTarget(Interceptor<Message> target) {
        this.target = target;
    }
}
