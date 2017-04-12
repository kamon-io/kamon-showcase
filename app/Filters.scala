import javax.inject.Inject

import filter.ConcurrentRequestsFilter
import kamon.play.KamonFilter
import play.api.http.DefaultHttpFilters
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter
import play.filters.hosts.AllowedHostsFilter

/**
 * Add the following filters by default to all projects
 * 
 * https://www.playframework.com/documentation/latest/ScalaCsrf 
 * https://www.playframework.com/documentation/latest/AllowedHostsFilter
 * https://www.playframework.com/documentation/latest/SecurityHeaders
 */
class Filters @Inject() (
  csrfFilter: CSRFFilter,
  allowedHostsFilter: AllowedHostsFilter,
  securityHeadersFilter: SecurityHeadersFilter,
  concurrentRequestsFilter: ConcurrentRequestsFilter
) extends DefaultHttpFilters(
  concurrentRequestsFilter,
  csrfFilter, 
  allowedHostsFilter, 
  securityHeadersFilter,
  new KamonFilter()
)