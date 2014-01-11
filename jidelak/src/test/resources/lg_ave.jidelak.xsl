<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/h:html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/h:html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>

	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant>
			<id>praha-lunch-garden-avenir</id>
			<name>Lunch Garden Avenir</name>
			<phone>+420 731 401 714</phone>
			<e-mail>pavel.pechaty.ml@cateringmelodie.cz</e-mail>
			<web>http://fgkavcihory.cateringmelodie.cz/cz/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Radlická 714/113a</address>
			<zip>158 00</zip>

			<source time="relative" base="week" firstDayOfWeek="mo"
				timeOffset="0" encoding="cp1250" dateFormat="dd. mmm. yyyy" locale="cs_CZ"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php" />
			<source time="relative" base="week" firstDayOfWeek="mo"
				timeOffset="1" encoding="cp1250" dateFormat="dd. mmm. yyyy" locale="cs_CZ"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-pristi-tyden-tisk.php" />

			<open>
				<term day-of-week="Po" from="8:00" to="17:00" />
				<term day-of-week="Út" from="8:00" to="17:00" />
				<term day-of-week="St" from="8:00" to="17:00" />
				<term day-of-week="Čt" from="8:00" to="17:00" />
				<term day-of-week="Pá" from="8:00" to="17:00" />
				<term date="1. 1. 2010" closed="true" />
			</open>

			<menu>
				<xsl:apply-templates
					select="//h:table[@class='tb_jidelak' and position() = 1]/h:tbody/h:tr[1]/h:td"
					mode="days" />
			</menu>
		</restaurant>
	</xsl:template>

	<xsl:template match="h:td" mode="days">
		<xsl:variable name="pos" select="position()" />
		<xsl:variable name="ref-time"
			select="substring-before(/h:html/h:body/h:div[@class='okno-tisk']/h:center/h:div[@class='vnitrek-tisk']/h:p[@class='centrovani velka']/h:b/text(), ' ') " />

		<xsl:apply-templates
			select="../../h:tr[2]/h:td[$pos]/h:p[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'normal'" />
			<xsl:with-param name="dish" select="'soup'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../h:tr[position() &gt; 2]/h:td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'normal'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::h:table[@class='tb_jidelak' and ( position() = 1)]/h:tbody/h:tr[2]/h:td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'superior'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::h:table[@class='tb_jidelak' and (position() = 2 )]/h:tbody/h:tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'live'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::h:table[@class='tb_jidelak' and position() = 3]/h:tbody/h:tr[2]/h:td[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'pasta'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="h:td|h:td/h:p">
		<xsl:param name="type" />
		<xsl:param name="dish" />
		<xsl:param name="time" />
		<xsl:param name="ref-time" />
		<meal order="{position()}" category="{$type}" dish="{$dish}"
			time="{$time}" ref-time="{$ref-time}">
			<title>
				<xsl:value-of select="." />
			</title>
			<description></description>
			<xsl:schoose>
				<xsl:when test="${category} = 'normal'">
					<price></price>
				</xsl:when>
				<xsl:otherwise>
					<price></price>

				</xsl:otherwise>
			</xsl:schoose>
		</meal>
	</xsl:template>

</xsl:stylesheet>