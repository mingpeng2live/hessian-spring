//package com;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import com.jolbox.bonecp.BoneCPDataSource;
//
///**
// * 实现动态数据源切换逻辑
// *
// * @author pengming
// * @Date  2015年10月30日 上午11:19:19
// */
//public class DynamicDataSource extends AbstractRoutingDataSource {
//	
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	
//	private Map<Object, Object> _targetDataSources;
//	
//
//	/**
//	 * 数据源为空或者为0时，自动切换至默认数据源，即在配置文件中定义的dataSource数据源
//	 */
//	@Override
//	protected Object determineCurrentLookupKey() {
//		String dataSourceName = DBContextHolder.getDBType();
//		if (StringUtils.isNotEmpty(dataSourceName)) {
//			dataSourceName = "dataSource";
//		} else {
//			this.selectDataSource(Integer.valueOf(dataSourceName));
//			if (dataSourceName.equals("0"))
//				dataSourceName = "dataSource";
//		}
//		logger.debug("--------> use datasource " + dataSourceName);
//		return dataSourceName;
//	}
//
//	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
//		this._targetDataSources = targetDataSources;
//		super.setTargetDataSources(this._targetDataSources);
//		afterPropertiesSet();
//	}
//
//	public void addTargetDataSource(String key, BoneCPDataSource dataSource) {
//		this._targetDataSources.put(key, dataSource);
//		this.setTargetDataSources(this._targetDataSources);
//	}
//
//	public BoneCPDataSource createDataSource(String driverClassName, String url,
//			String username, String password) {
//		BoneCPDataSource dataSource = new BoneCPDataSource();
//		dataSource.setDriverClass(driverClassName);
//		dataSource.setJdbcUrl(url);
//		dataSource.setUsername(username);
//		dataSource.setPassword(password);
//		return dataSource;
//	}
//
//	/**
//	 * @param serverId
//	 * @describe 数据源存在时不做处理，不存在时创建新的数据源链接，并将新数据链接添加至缓存
//	 */
//	public void selectDataSource(Integer serverId) {
//		Object sid = DBContextHolder.getDBType();
//		if ("0".equals(serverId + "")) {
//			DBContextHolder.setDBType("0");
//			return;
//		}
//		Object obj = this._targetDataSources.get(serverId);
//		if (obj != null && sid.equals(serverId + "")) {
//			return;
//		} else {
//			BoneCPDataSource dataSource = this.getDataSource(serverId);
//			if (null != dataSource)
//				this.setDataSource(serverId, dataSource);
//		}
//	}
//
//	/**
//	 * @describe 查询serverId对应的数据源记录
//	 * @param serverId
//	 * @return
//	 */
//	public BoneCPDataSource getDataSource(Integer serverId) {
//		this.selectDataSource(0);
//		this.determineCurrentLookupKey();
//		Connection conn = null;
//		HashMap<String, Object> map = null;
//		try {
//			conn = this.getConnection();
//			PreparedStatement ps = conn
//					.prepareStatement("SELECT * FROM bas_datasource WHERE DBS_ID = ?");
//			ps.setInt(1, serverId);
//			ResultSet rs = ps.executeQuery();
//			map = new HashMap<String, Object>();
//			if (rs.next()) {
//				map.put("DBS_ID", rs.getInt("DBS_ID"));
//				map.put("DBS_DriverClassName", rs
//						.getString("DBS_DriverClassName"));
//				map.put("DBS_URL", rs.getString("DBS_URL"));
//				map.put("DBS_UserName", rs.getString("DBS_UserName"));
//				map.put("DBS_Password", rs.getString("DBS_Password"));
//			}
//			rs.close();
//			ps.close();
//		} catch (SQLException e) {
//			logger.error("", e);
//		} finally {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				logger.error("", e);
//			}
//		}
//		if (null != map) {
//			String driverClassName = map.get("DBS_DriverClassName").toString();
//			String url = map.get("DBS_URL").toString();
//			String userName = map.get("DBS_UserName").toString();
//			String password = map.get("DBS_Password").toString();
//			BoneCPDataSource dataSource = this.createDataSource(driverClassName,
//					url, userName, password);
//			return dataSource;
//		}
//		return null;
//	}
//
//	/**
//	 * @param serverId
//	 * @param dataSource
//	 */
//	public void setDataSource(Integer serverId, BoneCPDataSource dataSource) {
//		this.addTargetDataSource(serverId + "", dataSource);
//		DBContextHolder.setDBType(serverId + "");
//	}
//
//}