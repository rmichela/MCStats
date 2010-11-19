
public class StatsListener extends PluginListener {

	private StatsManager psm;
	
	public StatsListener(StatsManager psm)
	{
		this.psm = psm;
	}
	
	@Override
	public boolean onBlockCreate(Player player, Block blockPlaced,
			Block blockClicked, int itemInHand) {
		
		psm.placeABlock(player, blockPlaced);
		
		return false;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		psm.destroyABlock(player, block);
		return false;
	}

	@Override
	public void onDisconnect(Player player) {
		
		psm.logOut(player);
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		
		psm.dropAnItem(player, item);
		
		return false;
	}

	@Override
	public void onLogin(Player player) {
		psm.logIn(player);
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		psm.travelAMeter(player);
	}
	
}
