package com.hunteron.hessian;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.StringUtils;

import com.caucho.hessian.client.HessianConnectionFactory;
import com.hunteron.hessian.connection.HessianHeaderCache;
import com.hunteron.hessian.connection.HessianHeaderURLConnectionFactory;

/**
 * hessian 接口客户端自动扫描注入
 * 
 * @author rocca.peng@hunteron.com
 * @Description
 * @Date 2015年2月8日 上午10:29:58
 */
public class HessianClientScannerConfigurer implements
		BeanDefinitionRegistryPostProcessor, InitializingBean,
		ApplicationContextAware, BeanNameAware {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private String beanName;
	private String basePackage;
	
	private Properties props;
	private Resource[] locations;

	private boolean includeAnnotationConfig = true;

	private ApplicationContext applicationContext;

	// 实现了该接口
	private Class<?> markerInterface;
	// 配置了该注解
	private Class<? extends Annotation> annotationClass;

	private BeanNameGenerator nameGenerator;
	
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 修改hessian链接工厂
		System.setProperty(HessianConnectionFactory.class.getName(), HessianHeaderURLConnectionFactory.class.getName());
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.locations, "Property 'locations' is required " + beanName);
		notNull(this.basePackage, "Property 'basePackage' is required " + beanName);
		loadProperties();
	}
	
	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		HessianClassPathScanner scan = new HessianClassPathScanner(registry);
		scan.setResourceLoader(this.applicationContext);
		scan.setBeanNameGenerator(this.nameGenerator);
		// 引入注解配置
		scan.setIncludeAnnotationConfig(this.includeAnnotationConfig);
		scan.registerFilters();

		String[] basePackages = StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		scan.scan(basePackages);
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
		this.includeAnnotationConfig = includeAnnotationConfig;
	}

	public Class<?> getMarkerInterface() {
		return markerInterface;
	}

	public void setMarkerInterface(Class<?> markerInterface) {
		this.markerInterface = markerInterface;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public BeanNameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(BeanNameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}
	
	public void setLocations(Resource... locations) {
		this.locations = locations;
	}
	
	protected void loadProperties() throws IOException {
		if (this.locations != null) {
			props = new Properties();
			for (Resource location : this.locations) {
				if (logger.isInfoEnabled()) {
					logger.info("Loading properties file from " + location);
				}
				try {
					PropertiesLoaderUtils.fillProperties(props, location);
				}catch (IOException ex) {
					if (logger.isWarnEnabled()) {
						logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
					}
				}
			}
		}
	}
	
	private class HessianClassPathScanner extends ClassPathBeanDefinitionScanner {

		public HessianClassPathScanner(BeanDefinitionRegistry registry) {
			super(registry, false);
		}

		@Override
		public Set<BeanDefinitionHolder> doScan(String... basePackages) {
			Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
			if (beanDefinitions.isEmpty()) {
				logger.warn("No hessian was found in '"
						+ Arrays.toString(basePackages)
						+ "' package. Please check your configuration.");
			} else {
				for (BeanDefinitionHolder holder : beanDefinitions) {
					GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

					if (logger.isDebugEnabled()) {
						logger.debug("Creating HessianProxyFactoryBean with name '"
								+ holder.getBeanName() + "' and '"
								+ definition.getBeanClassName()
								+ "' hessianInterface");
					}
					
					AnnotationMetadata metadata = ((ScannedGenericBeanDefinition)definition).getMetadata();
					Map<String, Object> annAtts = metadata.getAnnotationAttributes(annotationClass.getName());
					String context = (String)annAtts.get("context");
					String serverName = (String)annAtts.get("serverName");
					
					// 新增overloadEnabled属性，并把它的值设置为true，则Hessian就能支持方法的重载了，默认是false
					Boolean overloadEnabled = MapUtils.getBoolean(annAtts, "overloadEnabled", false);
					// true 表示context是配置的key,告诉扫描器从配置中读取路径, false 表示context是路径
					Boolean isKey = MapUtils.getBoolean(annAtts, "isKey", true);
					if (isKey) {
						context = props.getProperty(context);
					}
					// url 
					String url = context + serverName;
					// 请求头设置
					String[] headers = (String[])annAtts.get("headers");
					HessianHeaderCache.addHeaderCache(url, headers);
					
					// the hessian interface is the original class of the bean
					// but, the actual class of the bean is HessianProxyFactoryBean
					definition.getPropertyValues().add("serviceUrl", url);
					definition.getPropertyValues().add("serviceInterface", definition.getBeanClassName());
					definition.getPropertyValues().add("overloadEnabled", overloadEnabled);
					definition.setBeanClass(HessianProxyFactoryBean.class);
				}
			}
			return beanDefinitions;

		}

		@Override
		protected boolean isCandidateComponent(
				AnnotatedBeanDefinition beanDefinition) {
			return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean checkCandidate(String beanName,
				BeanDefinition beanDefinition) throws IllegalStateException {
			if (super.checkCandidate(beanName, beanDefinition)) {
				return true;
			} else {
				logger.warn("Skipping HessianProxyFactoryBean with name '" + beanName
						+ "' and '" + beanDefinition.getBeanClassName()
						+ "' hessianInterface"
						+ ". Bean already defined with the same name!");
				return false;
			}
		}

		public void registerFilters() {
			boolean acceptAllInterfaces = true;

		    // if specified, use the given annotation and / or marker interface
		    if (HessianClientScannerConfigurer.this.annotationClass != null) {
		      addIncludeFilter(new AnnotationTypeFilter(HessianClientScannerConfigurer.this.annotationClass));
		      acceptAllInterfaces = false;
		    }

		    // override AssignableTypeFilter to ignore matches on the actual marker interface
		    if (HessianClientScannerConfigurer.this.markerInterface != null) {
		      addIncludeFilter(new AssignableTypeFilter(HessianClientScannerConfigurer.this.markerInterface) {
		        @Override
		        protected boolean matchClassName(String className) {
		          return false;
		        }
		      });
		      acceptAllInterfaces = false;
		    }
			
			if (acceptAllInterfaces) {
				// default include filter that accepts all classes
				addIncludeFilter(new TypeFilter() {
					public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
						return true;
					}
				});
			}

			// exclude package-info.java
			addExcludeFilter(new TypeFilter() {
				public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
					String className = metadataReader.getClassMetadata().getClassName();
					return className.endsWith("package-info");
				}
			});
		}
	}
}
