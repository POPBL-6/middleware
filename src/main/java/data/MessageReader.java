package data;

import java.io.IOException;

public class MessageReader <T> {
	@SuppressWarnings("unchecked")
	public T readObject(MessagePublish msg) throws ClassNotFoundException, IOException {
		return (T)msg.getDataObject();
	}
}
