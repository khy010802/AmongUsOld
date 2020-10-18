package bepo.au.utils;

import java.lang.reflect.Field;

import net.minecraft.server.v1_16_R2.EnumProtocolDirection;
import net.minecraft.server.v1_16_R2.NetworkManager;

public class FixedNetworkManager extends NetworkManager {
	 
	 
    public FixedNetworkManager() {
        super(EnumProtocolDirection.CLIENTBOUND);
        try {
            try {
                this.swapFields();
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
 
    protected void swapFields() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field channelField = NetworkManager.class.getDeclaredField( "m" );
        channelField.setAccessible( true );
        channelField.set( this , new NPCChannel( null ) );
        channelField.setAccessible( false );
 
        Field socketAddressField = NetworkManager.class.getDeclaredField( "n" );
        socketAddressField.setAccessible( true );
        socketAddressField.set( this , null );
        socketAddressField.setAccessible( true );
        socketAddressField.set(this, null);
    }
}