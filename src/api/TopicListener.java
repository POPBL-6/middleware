package api;

import data.MessagePublication;

public interface TopicListener {
	
	void publicationReceived(MessagePublication message);
	
}
