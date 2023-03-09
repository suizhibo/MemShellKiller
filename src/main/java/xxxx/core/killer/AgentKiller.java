package xxxx.core.killer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;

public class AgentKiller extends KillerBase {
    @Override
    public String getType() {
        return "agent";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        try {
            InputStream in = loader.getResourceAsStream(className + ".class");
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while (true) {
                if (!((nRead = in.read(data, 0, data.length)) != -1)) break;
                buffer.write(data, 0, nRead);
            }
            classfileBuffer = buffer.toByteArray();
            return classfileBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
