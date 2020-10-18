package bepo.au.utils;

import java.net.SocketAddress;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;

public class NPCChannel extends AbstractChannel {

	protected final ChannelConfig config = new DefaultChannelConfig(this);

	protected NPCChannel(Channel parent) {
		super(parent);
	}

	@Override
	protected AbstractUnsafe newUnsafe() {
		return null;
	}

	@Override
	protected boolean isCompatible(EventLoop eventExecutors) {
		return true;
	}

	@Override
	protected SocketAddress localAddress0() {
		return null;
	}

	@Override
	protected SocketAddress remoteAddress0() {
		return null;
	}

	@Override
	protected void doBind(SocketAddress socketAddress) throws Exception {

	}

	@Override
	protected void doDisconnect() throws Exception {

	}

	@Override
	protected void doClose() throws Exception {

	}

	@Override
	protected void doBeginRead() throws Exception {

	}

	@Override
	protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) throws Exception {

	}

	@Override
	public ChannelConfig config() {
		return config;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public ChannelMetadata metadata() {
		return null;
	}
}
