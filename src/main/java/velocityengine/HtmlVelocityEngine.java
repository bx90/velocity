package velocityengine;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Base class for Velocity Engine.</br>
 * Initialize the object.
 *
 * @author bsun
 */
public class HtmlVelocityEngine {

    private VelocityEngine engine;
    private VelocityContext context;

    public static class Engine {
        private VelocityEngine veloxengine;
        private VelocityContext context;


        public Engine setVelocityEngine() {
            veloxengine = new VelocityEngine();
            return this;
        }

        public Engine setVelocityContext() {
            context = new VelocityContext();
            return this;
        }

        public HtmlVelocityEngine ignite() {
            return new HtmlVelocityEngine(this);
        }
    }


    private HtmlVelocityEngine(Engine initEngine) {
        this.engine = initEngine.veloxengine;
        this.context = initEngine.context;
    }

    public VelocityEngine getEngine() {
        return engine;
    }

    public VelocityContext getContext() {
        context.put("context", context);
        return context;
    }
}