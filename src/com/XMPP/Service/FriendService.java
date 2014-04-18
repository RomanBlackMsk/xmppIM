package com.XMPP.Service;

import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.XMPP.smack.Smack;
import com.XMPP.smack.SmackImpl;
import com.XMPP.util.L;

public class FriendService extends Service {

	Smack smack;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		smack = new SmackImpl();
		L.i("come into friend service onStartCommand");
		new Thread(new Runnable() {
			@Override
			public void run() {
				admitFriendsRequest();
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		L.i("come into friend service onCreate");

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void admitFriendsRequest() {
		final XMPPConnection connection = smack.getConnection();
		connection.getRoster().setSubscriptionMode(
				Roster.SubscriptionMode.manual);
		connection.addPacketListener(new PacketListener() {
			public void processPacket(Packet paramPacket) {

				if (paramPacket instanceof Presence) {
					Presence presence = (Presence) paramPacket;
					String email = presence.getFrom();
					L.i("accept an packet from " +email);

					System.out.println("presence: " + presence.getFrom()
							+ "; type: " + presence.getType() + "; to: "
							+ presence.getTo() + "; " + presence.toXML());
					Roster roster = connection.getRoster();

					if (presence.getType().equals(Presence.Type.subscribe)) {
						Presence newp = new Presence(Presence.Type.subscribed);
						newp.setMode(Presence.Mode.available);
						newp.setPriority(24);
						newp.setTo(presence.getFrom());
						connection.sendPacket(newp);
						Presence subscription = new Presence(
								Presence.Type.subscribe);
						subscription.setTo(presence.getFrom());
						connection.sendPacket(subscription);

					} else if (presence.getType().equals(
							Presence.Type.unsubscribe)) {
						Presence newp = new Presence(Presence.Type.unsubscribed);
						newp.setMode(Presence.Mode.available);
						newp.setPriority(24);
						newp.setTo(presence.getFrom());
						connection.sendPacket(newp);
					}
				}

			}
		}, new PacketFilter() {
			public boolean accept(Packet packet) {
				if (packet instanceof Presence) {
					Presence presence = (Presence) packet;
					if (presence.getType().equals(Presence.Type.subscribed)
							|| presence.getType().equals(
									Presence.Type.subscribe)
							|| presence.getType().equals(
									Presence.Type.unsubscribed)
							|| presence.getType().equals(
									Presence.Type.unsubscribe)) {
						return true;
					}
				}
				return false;
			}
		});

		connection.getRoster().addRosterListener(new RosterListener() {
			public void presenceChanged(Presence presence) {
				System.out.println(presence.getFrom() + "presenceChanged");

			}

			public void entriesUpdated(Collection<String> presence) {
				System.out.println("entriesUpdated");

			}

			public void entriesDeleted(Collection<String> presence) {
				System.out.println("entriesDeleted");

			}

			public void entriesAdded(Collection<String> presence) {
				System.out.println("entriesAdded");
			}
		});
	}

}