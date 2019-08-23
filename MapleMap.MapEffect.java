	public void startExtendedMapEffect(final String msg, final int itemId) {
		startMapEffect(msg, itemId, 60, true, -1);
		MapTimer.getInstance().schedule(() -> {
			startMapEffect(msg, itemId, 30, false, -1);
		}, 60000);
	}

	public void startJukebox(String msg, int itemId) {
		startMapEffect(msg, itemId, 300, true, -1);
	}

	public void startMapEffect(String msg, int itemId) {
		startMapEffect(msg, itemId, 30, false, -1);
	}

	public void startMapEffect(String msg, int itemId, boolean jukebox) {
		startMapEffect(msg, itemId, jukebox ? 300000 : 15000, jukebox, -1);
	}

	public void startMapEffect(String msg, int itemId, int time) {
		startMapEffect(msg, itemId, time, false, -1);
	}

	public void startMapEffect(String msg, int itemId, int time, int effectType) {
		startMapEffect(msg, itemId, time, false, effectType);
	}
	
	public void startMapEffect(String msg, int itemId, int time, boolean jukebox, int effectType) {
		if (mapEffect != null) {
			broadcastMessage(MaplePacketCreator.removeMapEffect());
		}
		if (time <= 0) {
			time = 5;
		}
		mapEffect = new MapleMapEffect(msg, itemId, effectType);
		mapEffect.setJukebox(jukebox);
		broadcastMessage(mapEffect.makeStartData());
		MapTimer.getInstance().schedule(() -> {
			if (mapEffect != null) {
				broadcastMessage(mapEffect.makeDestroyData());
				mapEffect = null;
			}
		}, time * 1000);
	}

	public void startPredictCardMapEffect(String msg, int itemId, int effectType) {
		startMapEffect(msg, itemId, 30, false, effectType);
	}

	public void startSimpleMapEffect(String msg, int itemId) {
		broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
	}

	public void stopMapEffect() {
		if (mapEffect != null) {
			broadcastMessage(mapEffect.makeDestroyData());
			mapEffect = null;
		}
	}
