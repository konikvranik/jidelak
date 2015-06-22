<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>
	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.0">
			<id>praha-babcak</id>
			<name>Jídelna Babčák</name>
			<phone>+420 266782055</phone>
			<web>http://www.restauraceumajeru.cz/</web>
			<city>Praha 7</city>
			<country>Česká republika</country>
			<address>Jankovcova 2</address>
			<zip>170 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				dateFormat="E, d MMM" locale="cs_CZ"
				url="https://www.zomato.com/cs/praha/j%C3%ADdelna-bab%C4%8D%C3%A1k-hole%C5%A1ovice-praha-7/menu#daily" />

			<open>
				<term from="11:00" to="15:00" description="denní menu" />
				<term day-of-week="Po" from="07:30" to="16:00" />
				<term day-of-week="Út" from="07:30" to="16:00" />
				<term day-of-week="St" from="07:30" to="16:00" />
				<term day-of-week="Čt" from="07:30" to="16:00" />
				<term day-of-week="Pá" from="07:30" to="15:00" />
			</open>

			<xsl:apply-templates select="//*[@id='daily-menu-container']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="*[@id='daily-menu-container']">
		<menu>
			<xsl:apply-templates
				select=".//div[@class='tmi-groups']/div[1]/div[starts-with(@class,'tmi ')]"
				mode="menuitem" />
		</menu>
	</xsl:template>

	<xsl:template match="div" mode="menuitem">
		<xsl:if
			test="not(contains(@class,'bold') or contains(., '2dcl točené limonády k obědu'))">
			<meal dish="" category="">
				<xsl:call-template name="dish" />
				<xsl:call-template name="category" />
				<xsl:call-template name="order" />
				<xsl:call-template name="time" />
				<xsl:call-template name="ref-time" />


				<xsl:call-template name="title" />
				<xsl:call-template name="description" />
				<xsl:call-template name="price" />
			</meal>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dish">
		<xsl:attribute name="dish"><xsl:choose>
		<xsl:when test="position() &lt; 3">soup</xsl:when>
		<xsl:otherwise>dinner</xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>

	<xsl:template name="category">
		<xsl:attribute name="category"><xsl:choose>
		<xsl:when
			test="count((preceding-sibling::div|.)[div[@class='tmi-text-group']/div[@class='tmi-name' and starts-with(normalize-space(.), 'TĚSTOVINY')]]) > 0">30-pasta</xsl:when>
		<xsl:when
			test="count((preceding-sibling::div|.)[div[@class='tmi-text-group']/div[@class='tmi-name' and starts-with(normalize-space(.), 'SALÁTY')]]) > 0">50-salad</xsl:when>
		<xsl:otherwise>10-normal</xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>

	<xsl:template name="title">
		<xsl:variable name="title">
			<xsl:choose>
				<xsl:when test="contains(.,'(')">
					<xsl:value-of select="normalize-space(substring-before(.,'('))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="normalize-space(.)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<title>
			<xsl:choose>
				<xsl:when test="starts-with($title,'Menu ')">
					<xsl:value-of select="normalize-space(substring-after($title,'Menu '))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="normalize-space($title)" />
				</xsl:otherwise>
			</xsl:choose>
		</title>
	</xsl:template>

	<xsl:template name="description">
		<xsl:choose>
			<xsl:when test="contains(.,'(')">
				<description>
					<xsl:value-of
						select="normalize-space(substring-after(substring-before(.,')'),'('))" />
				</description>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="price">
		<xsl:choose>
			<xsl:when test="contains(.,')')">
				<price>
					<xsl:value-of select="normalize-space(substring-after(.,')'))" />
				</price>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="order">
		<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
	</xsl:template>
	<xsl:template name="time">
		<xsl:variable name="time">
			<xsl:value-of select="preceding-sibling::div[@class='tmi-group-name']" />
		</xsl:variable>
		<xsl:attribute name="time"><xsl:choose>
		<xsl:when test="contains($time,'(')"><xsl:value-of
			select="normalize-space(substring-before($time,'('))" /></xsl:when>
		<xsl:otherwise><xsl:value-of select="normalize-space($time)" /></xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>

	<xsl:template name="ref-time">
		<xsl:variable name="time">
			<xsl:value-of select="preceding-sibling::div[@class='tmi-group-name']" />
		</xsl:variable>
		<xsl:attribute name="ref-time"><xsl:choose>
		<xsl:when test="contains($time,'(')"><xsl:value-of
			select="normalize-space(substring-before($time,'('))" /></xsl:when>
		<xsl:otherwise><xsl:value-of select="normalize-space($time)" /></xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>


</xsl:stylesheet>
