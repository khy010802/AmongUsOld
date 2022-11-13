package bepo.au.function;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;

import bepo.au.GameTimer.Status;
import bepo.au.Main;

public class SoundRemover {
	
	public static void addListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					
					@Override
					public void onPacketSending(PacketEvent event) {
						
						if(Main.gt == null || Main.gt.getStatus() != Status.WORKING) return;
						
						if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
							SoundCategory sc = event.getPacket().getSoundCategories().read(0);
                            if(sc == SoundCategory.PLAYERS){
                                 event.setCancelled(true);
                            }
                        }
					}
					
					
				}
				
				);
	}

}
