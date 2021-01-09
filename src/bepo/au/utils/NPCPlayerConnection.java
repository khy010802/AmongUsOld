package bepo.au.utils;

import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import net.minecraft.server.v1_16_R2.NetworkManager;
import net.minecraft.server.v1_16_R2.Packet;
import net.minecraft.server.v1_16_R2.PacketPlayInBlockDig;
import net.minecraft.server.v1_16_R2.PacketPlayInBlockPlace;
import net.minecraft.server.v1_16_R2.PacketPlayInChat;
import net.minecraft.server.v1_16_R2.PacketPlayInFlying;
import net.minecraft.server.v1_16_R2.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_16_R2.PacketPlayInTransaction;
import net.minecraft.server.v1_16_R2.PacketPlayInUpdateSign;
import net.minecraft.server.v1_16_R2.PacketPlayInWindowClick;
import net.minecraft.server.v1_16_R2.PlayerConnection;

public class NPCPlayerConnection extends PlayerConnection {
	 
    public NPCPlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }
 
    @Override
    public void a(PacketPlayInWindowClick packet) {
    }
 
    @Override
    public void a(PacketPlayInTransaction packet) {
    }
 
    @Override
    public void a(PacketPlayInFlying packet) {
    }
 
    @Override
    public void a(PacketPlayInUpdateSign packet) {
    }
 
    @Override
    public void a(PacketPlayInBlockDig packet) {
    }
 
    @Override
    public void a(PacketPlayInBlockPlace packet) {
    }
 
    @Override
    public void disconnect(String s) {
    }
 
    @Override
    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
    }
 
    @Override
    public void a(PacketPlayInChat packetplayinchat) {
    }
 
    @SuppressWarnings("rawtypes")
	@Override
    public void sendPacket(Packet packet) {
    }
}