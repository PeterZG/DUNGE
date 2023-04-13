package dungeonmania;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCopier {
    public static Object copy(Object oldObj) throws IOException, ClassNotFoundException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(oldObj);
            objOut.flush();
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                 ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
                return objIn.readObject();
            }
        }
    }
}
