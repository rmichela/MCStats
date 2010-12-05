//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


public class StatsPluginListener extends PluginListener {

	private StatsController psm;
	
	public StatsPluginListener(StatsController psm)
	{
		this.psm = psm;
	}
	
	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		
		psm.placeABlock(player, blockPlaced);
		return super.onBlockPlace(player, blockPlaced, blockClicked, itemInHand);
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		psm.destroyABlock(player, block);
		return super.onBlockBreak(player, block);
	}

	@Override
	public void onDisconnect(Player player) {
		
		psm.logOut(player);
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		
		psm.dropAnItem(player, item);
		
		return super.onItemDrop(player, item);
	}

	@Override
	public void onLogin(Player player) {
		psm.logIn(player);
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		psm.travelAMeter(player);
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		psm.healthChange(player, oldValue, newValue);
		return super.onHealthChange(player, oldValue, newValue);
	}
	
}